package com.lockify.phase1.coreauth.service;

import com.lockify.phase1.coreauth.dto.*;
import com.lockify.phase1.coreauth.entity.RefreshToken;
import com.lockify.phase1.coreauth.mapper.UserMapper;
import com.lockify.phase1.coreauth.repository.RefreshTokenRepository;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import com.lockify.phase1.coreauth.security.jwt.JwtService;
import com.lockify.phase2.account.service.AccountLockService;
import com.lockify.phase2.account.service.EmailVerificationService;
import com.lockify.phase2.account.service.PasswordPolicyService;
import com.lockify.phase3.session.service.SessionTrackingService;
import com.lockify.phase4.enterprise.audit.service.AuditService;
import com.lockify.phase7.hardening.service.AdaptiveAuthService;
import com.lockify.phase7.hardening.service.DeviceFingerprintService;
import com.lockify.phase7.hardening.service.TokenRotationService;
import com.lockify.shared.config.JwtProperties;
import com.lockify.shared.domain.entity.*;
import com.lockify.shared.domain.repository.RoleRepository;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import com.lockify.shared.exception.DuplicateResourceException;
import com.lockify.shared.exception.TokenException;
import com.lockify.shared.util.ClientInfoExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import com.lockify.shared.security.CredentialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Auth Service - Phase 1 core + cross-phase integration hub.
 * Register/Login/Refresh ke saath Phase 2-7 services bhi trigger hote hain.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CredentialService credentialService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;

    // Cross-phase integrations
    private final EmailVerificationService emailVerificationService;
    private final PasswordPolicyService passwordPolicyService;
    private final AccountLockService accountLockService;
    private final SessionTrackingService sessionTrackingService;
    private final AuditService auditService;
    private final TokenRotationService tokenRotationService;
    private final DeviceFingerprintService deviceFingerprintService;
    private final AdaptiveAuthService adaptiveAuthService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Yeh email pehle se registered hai");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Yeh username pehle se le liya gaya hai");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalStateException("USER role database me nahi hai"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .enabled(true)
                .accountLocked(false)
                .emailVerified(false)
                .roles(Set.of(userRole))
                .build();

        User saved = userRepository.save(user);
        credentialService.createCredentialsForUser(saved, request.getPassword());

        // Phase 2 - email verification token bhejo
        emailVerificationService.sendVerificationEmail(saved);

        auditService.logAction(saved.getId(), "USER_REGISTERED", "USER", "username=" + saved.getUsername());
        return UserMapper.toResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String ip = ClientInfoExtractor.extractIp(httpRequest);

        // Phase 2 - pehle check karo account locked to nahi
        userRepository.findByIdentifierWithCredential(request.getIdentifier())
                .ifPresent(accountLockService::ensureAccountNotLocked);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
            );

            LockifyUserDetails userDetails = (LockifyUserDetails) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new AuthenticationException("User nahi mila"));

            // Phase 2 - password expiry check
            passwordPolicyService.ensurePasswordNotExpired(user);

            accountLockService.recordSuccessfulLogin(user, ip);
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);

            // Phase 3 - session track karo
            sessionTrackingService.createSession(user, httpRequest);

            // Phase 7 - device fingerprint + adaptive auth
            String userAgent = ClientInfoExtractor.extractUserAgent(httpRequest);
            String fingerprint = userAgent + "|" + ip;
            deviceFingerprintService.registerOrRecognize(
                    user, fingerprint,
                    ClientInfoExtractor.parseBrowser(userAgent),
                    ClientInfoExtractor.parseOs(userAgent),
                    httpRequest.getHeader("X-Screen-Resolution"),
                    httpRequest.getHeader("X-Timezone")
            );
            adaptiveAuthService.evaluate(user, fingerprint, ip, httpRequest.getHeader("X-Country"));

            auditService.logAction(user.getId(), "USER_LOGIN", "AUTH", "ip=" + ip);
            return buildAuthResponse(userDetails);

        } catch (BadCredentialsException e) {
            accountLockService.recordFailedLogin(request.getIdentifier(), ip);
            auditService.logAction(null, "LOGIN_FAILED", "AUTH", "identifier=" + request.getIdentifier());
            throw new AuthenticationException("Invalid email/username ya password");
        } catch (DisabledException e) {
            throw new AuthenticationException("Account disabled hai");
        } catch (LockedException e) {
            throw new AuthenticationException("Account locked hai");
        }
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        // Phase 7 - token rotation with family tracking
        TokenRotationService.RotationResult rotation = tokenRotationService.rotateRefreshToken(request.getRefreshToken());
        User user = rotation.user();

        if (!user.isEnabled() || user.isAccountLocked() || accountLockService.isAccountLocked(user)) {
            throw new AuthenticationException("Account access allowed nahi hai");
        }

        LockifyUserDetails userDetails = new LockifyUserDetails(user);
        return buildAuthResponse(userDetails, rotation.newRefreshToken());
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(LockifyUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));
        return UserMapper.toResponse(user);
    }

    private AuthResponse buildAuthResponse(LockifyUserDetails userDetails) {
        String rawRefresh = jwtService.generateOpaqueRefreshToken();
        return buildAuthResponse(userDetails, rawRefresh);
    }

    private AuthResponse buildAuthResponse(LockifyUserDetails userDetails, String rawRefreshToken) {
        Set<String> roles = userDetails.getRoleNames();
        Set<String> permissions = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(
                userDetails.getId(), userDetails.getUsername(), roles, permissions);

        String refreshTokenHash = jwtService.hashToken(rawRefreshToken);
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));

        Instant refreshExpiry = Instant.now().plusSeconds(
                (long) jwtProperties.getRefreshTokenExpiryDays() * 24 * 60 * 60);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(refreshTokenHash)
                .user(user)
                .expiresAt(refreshExpiry)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getAccessTokenExpirySeconds())
                .user(UserMapper.toResponse(userDetails))
                .build();
    }
}
