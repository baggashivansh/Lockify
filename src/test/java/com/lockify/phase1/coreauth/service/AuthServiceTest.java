package com.lockify.phase1.coreauth.service;

import com.lockify.phase1.coreauth.dto.RegisterRequest;
import com.lockify.phase1.coreauth.repository.RefreshTokenRepository;
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
import com.lockify.shared.domain.entity.Role;
import com.lockify.shared.domain.entity.RoleName;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.RoleRepository;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.DuplicateResourceException;
import com.lockify.shared.security.CredentialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private CredentialService credentialService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EmailVerificationService emailVerificationService;
    @Mock private PasswordPolicyService passwordPolicyService;
    @Mock private AccountLockService accountLockService;
    @Mock private SessionTrackingService sessionTrackingService;
    @Mock private AuditService auditService;
    @Mock private TokenRotationService tokenRotationService;
    @Mock private DeviceFingerprintService deviceFingerprintService;
    @Mock private AdaptiveAuthService adaptiveAuthService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-minimum-32-characters-long!!");
        properties.setAccessTokenExpiryMinutes(15);
        properties.setRefreshTokenExpiryDays(7);
        properties.setIssuer("lockify-test");

        JwtService jwtService = new JwtService(properties, new ObjectMapper());
        authService = new AuthService(
                userRepository, roleRepository, refreshTokenRepository,
                credentialService, jwtService, properties, authenticationManager,
                emailVerificationService, passwordPolicyService, accountLockService,
                sessionTrackingService, auditService, tokenRotationService,
                deviceFingerprintService, adaptiveAuthService
        );
    }

    @Test
    @DisplayName("Valid registration - credential service call honi chahiye")
    void validRegistration() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john").email("john@example.com").password("Password@123").build();
        Role userRole = Role.builder().id(1L).name(RoleName.USER).build();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        authService.register(request);

        verify(credentialService).createCredentialsForUser(any(), eq("Password@123"));
        verify(emailVerificationService).sendVerificationEmail(any());
    }

    @Test
    @DisplayName("Duplicate email pe conflict")
    void duplicateEmail() {
        when(userRepository.existsByEmail(any())).thenReturn(true);
        assertThatThrownBy(() -> authService.register(
                RegisterRequest.builder().username("a").email("x@y.com").password("Password@123").build()
        )).isInstanceOf(DuplicateResourceException.class);
    }
}
