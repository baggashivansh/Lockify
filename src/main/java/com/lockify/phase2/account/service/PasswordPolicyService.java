package com.lockify.phase2.account.service;

import com.lockify.phase2.account.config.AccountSecurityProperties;
import com.lockify.phase2.account.entity.PasswordHistory;
import com.lockify.phase2.account.repository.PasswordHistoryRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.exception.LockifyException;
import com.lockify.shared.security.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordPolicyService {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialService credentialService;
    private final AccountSecurityProperties properties;

    public void validatePasswordNotInHistory(User user, String rawPassword) {
        String currentHash = credentialService.getPasswordHash(user);
        if (passwordEncoder.matches(rawPassword, currentHash)) {
            throw new LockifyException("Naya password purane se different hona chahiye", HttpStatus.BAD_REQUEST);
        }

        List<PasswordHistory> history = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(
                user, PageRequest.of(0, properties.getPasswordHistoryCount()));

        for (PasswordHistory entry : history) {
            if (passwordEncoder.matches(rawPassword, entry.getPasswordHash())) {
                throw new LockifyException(
                        "Yeh password recently use ho chuka hai - naya password choose karo",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    public void savePasswordHistory(User user) {
        passwordHistoryRepository.save(PasswordHistory.builder()
                .user(user)
                .passwordHash(credentialService.getPasswordHash(user))
                .build());
    }

    public void ensurePasswordNotExpired(User user) {
        credentialService.ensurePasswordNotExpired(user);
    }
}
