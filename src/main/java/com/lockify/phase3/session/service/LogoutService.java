package com.lockify.phase3.session.service;

import com.lockify.phase2.account.dto.MessageResponse;
import com.lockify.phase1.coreauth.repository.RefreshTokenRepository;
import com.lockify.phase3.session.repository.UserSessionRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logout Service - current session ya saari sessions + refresh tokens revoke karta hai.
 */
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final SessionTrackingService sessionTrackingService;
    private final UserRepository userRepository;

    /** Current session logout karo */
    @Transactional
    public MessageResponse logoutCurrent(Long userId, String sessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));

        if (sessionId != null && !sessionId.isBlank()) {
            sessionTrackingService.revokeSession(user, sessionId);
        }

        return MessageResponse.of("Logout successful");
    }

    /** Saari sessions aur refresh tokens revoke karo - logout everywhere */
    @Transactional
    public MessageResponse logoutAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));

        refreshTokenRepository.revokeAllByUser(user);
        userSessionRepository.deactivateAllByUser(user);

        return MessageResponse.of("Saari sessions revoke ho gayi hain");
    }
}
