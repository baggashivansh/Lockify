package com.lockify.phase2.account.service;

import com.lockify.phase2.account.config.AccountSecurityProperties;
import com.lockify.phase2.account.entity.LoginAttempt;
import com.lockify.phase2.account.repository.LoginAttemptRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import com.lockify.shared.security.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AccountLockService {

    private static final String REDIS_LOCK_PREFIX = "lockify:account-lock:";

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final CredentialService credentialService;
    private final StringRedisTemplate redisTemplate;
    private final AccountSecurityProperties properties;

    @Transactional
    public void recordFailedLogin(String identifier, String ipAddress) {
        loginAttemptRepository.save(LoginAttempt.builder()
                .identifier(identifier).ipAddress(ipAddress).successful(false).build());

        userRepository.findByIdentifierWithCredential(identifier).ifPresent(user -> {
            credentialService.recordFailedAttempt(user);
            if (user.getFailedLoginAttempts() >= properties.getMaxFailedAttempts()) {
                lockAccount(user);
            }
        });
    }

    @Transactional
    public void recordSuccessfulLogin(User user, String ipAddress) {
        loginAttemptRepository.save(LoginAttempt.builder()
                .identifier(user.getEmail()).ipAddress(ipAddress).successful(true).build());
        credentialService.resetFailedAttempts(user);
        redisTemplate.delete(REDIS_LOCK_PREFIX + user.getId());
    }

    public boolean isAccountLocked(User user) {
        if (redisTemplate.opsForValue().get(REDIS_LOCK_PREFIX + user.getId()) != null) {
            return true;
        }
        return user.isTemporarilyLocked();
    }

    @Transactional
    public void lockAccount(User user) {
        Instant lockedUntil = Instant.now().plus(properties.getLockDurationMinutes(), ChronoUnit.MINUTES);
        credentialService.lockUntil(user, lockedUntil);
        redisTemplate.opsForValue().set(
                REDIS_LOCK_PREFIX + user.getId(),
                lockedUntil.toString(),
                Duration.ofMinutes(properties.getLockDurationMinutes())
        );
    }

    public void ensureAccountNotLocked(User user) {
        if (isAccountLocked(user)) {
            throw new AuthenticationException("Account temporarily locked hai - baad me try karo");
        }
    }
}
