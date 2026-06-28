package com.lockify.phase4.enterprise.mfa.service;

import com.lockify.phase4.enterprise.audit.annotation.Auditable;
import com.lockify.phase4.enterprise.mfa.dto.MfaEnableRequest;
import com.lockify.phase4.enterprise.mfa.dto.MfaSetupResponse;
import com.lockify.phase4.enterprise.mfa.dto.MfaVerifyRequest;
import com.lockify.phase4.enterprise.mfa.entity.MfaConfiguration;
import com.lockify.phase4.enterprise.mfa.entity.OtpCode;
import com.lockify.phase4.enterprise.mfa.repository.MfaConfigurationRepository;
import com.lockify.phase4.enterprise.mfa.repository.OtpCodeRepository;
import com.lockify.phase1.coreauth.security.jwt.JwtService;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * MFA setup, verify, enable/disable - TOTP + email OTP dono support.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private static final String OTP_TYPE_EMAIL = "EMAIL";
    private static final String ISSUER = "Lockify";

    private final MfaConfigurationRepository mfaConfigurationRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final UserRepository userRepository;
    private final TotpService totpService;
    private final JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public MfaSetupResponse setupTotp(Long userId) {
        User user = loadUser(userId);
        String secret = totpService.generateSecret();
        String otpAuthUri = totpService.buildOtpAuthUri(ISSUER, user.getEmail(), secret);

        MfaConfiguration config = mfaConfigurationRepository.findByUser(user)
                .orElse(MfaConfiguration.builder().user(user).build());
        config.setTotpSecret(secret);
        config.setMfaEnabled(false);
        mfaConfigurationRepository.save(config);

        return MfaSetupResponse.builder()
                .secret(secret)
                .otpAuthUri(otpAuthUri)
                .mfaEnabled(false)
                .message("Authenticator app se scan karo, phir /enable pe code bhejo")
                .build();
    }

    @Transactional(readOnly = true)
    public boolean verify(Long userId, MfaVerifyRequest request) {
        MfaConfiguration config = getConfigOrThrow(userId);
        if (OTP_TYPE_EMAIL.equalsIgnoreCase(request.getType())) {
            return verifyEmailOtp(loadUser(userId), request.getCode());
        }
        if (config.getTotpSecret() == null) {
            throw new AuthenticationException("Pehle TOTP setup karo");
        }
        return totpService.verifyCode(config.getTotpSecret(), request.getCode());
    }

    @Transactional
    @Auditable(action = "MFA_ENABLE", resource = "MFA")
    public void enable(Long userId, MfaEnableRequest request) {
        MfaConfiguration config = getConfigOrThrow(userId);
        if (!totpService.verifyCode(config.getTotpSecret(), request.getCode())) {
            throw new AuthenticationException("Invalid TOTP code");
        }
        config.setMfaEnabled(true);
        config.setEmailOtpEnabled(request.isEnableEmailOtp());
        mfaConfigurationRepository.save(config);
    }

    @Transactional
    @Auditable(action = "MFA_DISABLE", resource = "MFA")
    public void disable(Long userId, MfaVerifyRequest request) {
        if (!verify(userId, request)) {
            throw new AuthenticationException("MFA verify fail - disable allowed nahi");
        }
        MfaConfiguration config = getConfigOrThrow(userId);
        config.setMfaEnabled(false);
        config.setEmailOtpEnabled(false);
        config.setTotpSecret(null);
        mfaConfigurationRepository.save(config);
    }

    @Transactional
    public void generateEmailOtp(Long userId) {
        User user = loadUser(userId);
        String rawCode = String.format("%06d", secureRandom.nextInt(1_000_000));
        String codeHash = jwtService.hashToken(rawCode);

        OtpCode otp = OtpCode.builder()
                .user(user)
                .codeHash(codeHash)
                .type(OTP_TYPE_EMAIL)
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .used(false)
                .build();
        otpCodeRepository.save(otp);

        // Production me email service se bhejoge - dev me log
        log.info("Email OTP userId={} (dev only - production me email jayega)", userId);
    }

    @Transactional(readOnly = true)
    public boolean isMfaEnabled(Long userId) {
        return mfaConfigurationRepository.findByUserId(userId)
                .map(MfaConfiguration::isMfaEnabled)
                .orElse(false);
    }

    private boolean verifyEmailOtp(User user, String code) {
        OtpCode otp = otpCodeRepository
                .findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, OTP_TYPE_EMAIL)
                .orElseThrow(() -> new AuthenticationException("Email OTP generate nahi hua"));

        if (!otp.isValid()) {
            throw new AuthenticationException("OTP expire ho chuka hai");
        }

        boolean match = jwtService.hashToken(code).equals(otp.getCodeHash());
        if (match) {
            otp.setUsed(true);
            otpCodeRepository.save(otp);
        }
        return match;
    }

    private MfaConfiguration getConfigOrThrow(Long userId) {
        return mfaConfigurationRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthenticationException("MFA setup nahi hua"));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));
    }
}
