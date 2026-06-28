package com.lockify.shared.security;

import com.lockify.phase2.account.config.AccountSecurityProperties;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.entity.UserCredential;
import com.lockify.shared.domain.entity.UserProfile;
import com.lockify.shared.domain.repository.UserCredentialRepository;
import com.lockify.shared.domain.repository.UserProfileRepository;
import com.lockify.shared.exception.LockifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * CredentialService - SAARI password operations yahan se hongi.
 *
 * Single source of truth for:
 * - Password hashing (peppered BCrypt)
 * - Credential creation on register
 * - Password updates on reset
 * - Lock state management
 *
 * Kabhi bhi password hash directly User entity pe mat set karo.
 */
@Service
@RequiredArgsConstructor
public class CredentialService {

    private final UserCredentialRepository credentialRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountSecurityProperties accountSecurityProperties;

    /** Registration pe credential + profile dono banao */
    @Transactional
    public void createCredentialsForUser(User user, String rawPassword) {
        Instant now = Instant.now();

        UserCredential credential = UserCredential.builder()
                .user(user)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .hashAlgorithm("BCRYPT")
                .hashStrength(12)
                .pepperVersion(1)
                .failedLoginAttempts(0)
                .passwordChangedAt(now)
                .passwordExpiresAt(now.plus(accountSecurityProperties.getPasswordExpiryDays(), ChronoUnit.DAYS))
                .mfaRequired(false)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .displayName(user.getUsername())
                .timezone("UTC")
                .locale("en")
                .build();

        user.setCredential(credential);
        user.setProfile(profile);
        credentialRepository.save(credential);
        profileRepository.save(profile);
    }

    /** Password reset / change */
    @Transactional
    public void updatePassword(User user, String newRawPassword) {
        UserCredential credential = getOrThrow(user);
        credential.setPasswordHash(passwordEncoder.encode(newRawPassword));
        credential.setPasswordChangedAt(Instant.now());
        credential.setPasswordExpiresAt(Instant.now().plus(
                accountSecurityProperties.getPasswordExpiryDays(), ChronoUnit.DAYS));
        credentialRepository.save(credential);
    }

    @Transactional
    public void recordFailedAttempt(User user) {
        UserCredential credential = getOrThrow(user);
        credential.setFailedLoginAttempts(credential.getFailedLoginAttempts() + 1);
        credentialRepository.save(credential);
    }

    @Transactional
    public void resetFailedAttempts(User user) {
        UserCredential credential = getOrThrow(user);
        credential.setFailedLoginAttempts(0);
        credential.setLockedUntil(null);
        credentialRepository.save(credential);
    }

    @Transactional
    public void lockUntil(User user, Instant until) {
        UserCredential credential = getOrThrow(user);
        credential.setLockedUntil(until);
        credentialRepository.save(credential);
    }

    public void ensurePasswordNotExpired(User user) {
        UserCredential credential = getOrThrow(user);
        if (credential.isPasswordExpired()) {
            throw new LockifyException("Password expire ho chuka hai - reset karo", HttpStatus.UNAUTHORIZED);
        }
    }

    public String getPasswordHash(User user) {
        return getOrThrow(user).getPasswordHash();
    }

    private UserCredential getOrThrow(User user) {
        if (user.getCredential() != null) {
            return user.getCredential();
        }
        return credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("User credential missing for id: " + user.getId()));
    }
}
