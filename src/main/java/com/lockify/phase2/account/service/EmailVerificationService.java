package com.lockify.phase2.account.service;

import com.lockify.phase2.account.config.AccountSecurityProperties;
import com.lockify.phase2.account.dto.MessageResponse;
import com.lockify.phase2.account.entity.EmailVerification;
import com.lockify.phase2.account.repository.EmailVerificationRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import com.lockify.shared.exception.LockifyException;
import com.lockify.shared.exception.TokenException;
import com.lockify.shared.util.TokenHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Email Verification Service - registration ke baad email confirm karne ka flow.
 *
 * Abhi emails console me log hoti hain - production me SMTP integrate karna hoga.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final AccountSecurityProperties properties;

    /**
     * Registration ke baad verification email bhejo (console log).
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        String rawToken = TokenHasher.generateSecureToken();
        String tokenHash = TokenHasher.sha256(rawToken);

        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(properties.getEmailVerificationHours(), ChronoUnit.HOURS))
                .used(false)
                .build();

        emailVerificationRepository.save(verification);

        // Dev mode - production me real email service use karo
        log.info("[EMAIL] Verification email for {} — token: {}", user.getEmail(), rawToken);
    }

    /** Token verify karke user ka email verified mark karo */
    @Transactional
    public MessageResponse verifyEmail(String token) {
        String tokenHash = TokenHasher.sha256(token);

        EmailVerification verification = emailVerificationRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenException("Invalid ya expired verification token"));

        if (!verification.isValid()) {
            throw new TokenException("Verification token expire ho gaya hai ya pehle use ho chuka hai");
        }

        verification.setUsed(true);
        emailVerificationRepository.save(verification);

        User user = verification.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return MessageResponse.of("Email successfully verified ho gaya");
    }

    /** Logged-in user ke liye verification email dubara bhejo */
    @Transactional
    public MessageResponse resendVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));

        if (user.isEmailVerified()) {
            throw new LockifyException("Email pehle se verified hai", HttpStatus.BAD_REQUEST);
        }

        sendVerificationEmail(user);
        return MessageResponse.of("Verification email dubara bhej di gayi hai");
    }
}
