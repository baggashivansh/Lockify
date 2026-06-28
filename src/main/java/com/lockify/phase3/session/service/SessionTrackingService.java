package com.lockify.phase3.session.service;

import com.lockify.phase3.session.dto.SessionResponse;
import com.lockify.phase3.session.entity.UserSession;
import com.lockify.phase3.session.repository.UserSessionRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.exception.AuthenticationException;
import com.lockify.shared.exception.LockifyException;
import com.lockify.shared.util.ClientInfoExtractor;
import com.lockify.shared.util.TokenHasher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Session Tracking Service - login pe session create, list aur revoke karta hai.
 */
@Service
@RequiredArgsConstructor
public class SessionTrackingService {

    private final UserSessionRepository userSessionRepository;

    /** Login ke baad naya session record banao */
    @Transactional
    public UserSession createSession(User user, HttpServletRequest request) {
        String userAgent = ClientInfoExtractor.extractUserAgent(request);

        UserSession session = UserSession.builder()
                .user(user)
                .sessionId(TokenHasher.generateSecureToken())
                .deviceName(request.getHeader("X-Device-Name"))
                .browser(ClientInfoExtractor.parseBrowser(userAgent))
                .os(ClientInfoExtractor.parseOs(userAgent))
                .ipAddress(ClientInfoExtractor.extractIp(request))
                .active(true)
                .loginAt(Instant.now())
                .lastActivity(Instant.now())
                .build();

        return userSessionRepository.save(session);
    }

    /** User ki saari active sessions list karo */
    @Transactional(readOnly = true)
    public List<SessionResponse> listActiveSessions(User user) {
        return userSessionRepository.findByUserAndActiveTrueOrderByLastActivityDesc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Ek specific session revoke karo */
    @Transactional
    public void revokeSession(User user, String sessionId) {
        UserSession session = userSessionRepository.findBySessionIdAndUser(sessionId, user)
                .orElseThrow(() -> new LockifyException("Session nahi mili", HttpStatus.NOT_FOUND));

        session.setActive(false);
        session.setLogoutAt(Instant.now());
        userSessionRepository.save(session);
    }

    /** Session activity timestamp update karo */
    @Transactional
    public void touchSession(String sessionId) {
        userSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            if (session.isActive()) {
                session.setLastActivity(Instant.now());
                userSessionRepository.save(session);
            }
        });
    }

    private SessionResponse toResponse(UserSession session) {
        return SessionResponse.builder()
                .sessionId(session.getSessionId())
                .deviceName(session.getDeviceName())
                .browser(session.getBrowser())
                .os(session.getOs())
                .ipAddress(session.getIpAddress())
                .active(session.isActive())
                .loginAt(session.getLoginAt())
                .lastActivity(session.getLastActivity())
                .build();
    }
}
