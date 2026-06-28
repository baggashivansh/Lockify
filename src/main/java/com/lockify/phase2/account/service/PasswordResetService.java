package com.lockify.phase2.account.service;

import com.lockify.phase2.account.config.AccountSecurityProperties;
import com.lockify.phase2.account.dto.MessageResponse;
import com.lockify.phase2.account.entity.PasswordReset;
import com.lockify.phase2.account.repository.PasswordResetRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.TokenException;
import com.lockify.shared.util.TokenHasher;
import com.lockify.shared.security.CredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Password Reset Service - forgot password aur reset password flow handle karta hai.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final CredentialService credentialService;
    private final AccountSecurityProperties properties;

    /**
     * Reset link bhejo - security ke liye hamesha same message return karo
     * chahe email exist kare ya na kare.
     */
    @Transactional
    public MessageResponse forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String rawToken = TokenHasher.generateSecureToken();
            String tokenHash = TokenHasher.sha256(rawToken);

            PasswordReset reset = PasswordReset.builder()
                    .user(user)
                    .tokenHash(tokenHash)
                    .expiresAt(Instant.now().plus(properties.getPasswordResetHours(), ChronoUnit.HOURS))
                    .used(false)
                    .build();

            passwordResetRepository.save(reset);

            // Dev mode - production me real email service use karo
            log.info("[EMAIL] Password reset for {} — token: {}", email, rawToken);
        });

        return MessageResponse.of("Agar email registered hai to reset link bhej di gayi hai");
    }

    /** Token validate karke naya password set karo */
    @Transactional
    public MessageResponse resetPassword(String token, String newPassword) {
        String tokenHash = TokenHasher.sha256(token);

        PasswordReset reset = passwordResetRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenException("Invalid ya expired reset token"));

        if (!reset.isValid()) {
            throw new TokenException("Reset token expire ho gaya hai ya pehle use ho chuka hai");
        }

        User user = reset.getUser();
        passwordPolicyService.validatePasswordNotInHistory(user, newPassword);

        passwordPolicyService.savePasswordHistory(user);
        credentialService.updatePassword(user, newPassword);

        reset.setUsed(true);
        passwordResetRepository.save(reset);

        return MessageResponse.of("Password successfully reset ho gaya");
    }
}
