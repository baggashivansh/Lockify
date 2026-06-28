package com.lockify.phase7.hardening.service;

import com.lockify.phase7.hardening.dto.SecurityEventResponse;
import com.lockify.phase7.hardening.entity.SecurityEvent;
import com.lockify.phase7.hardening.repository.SecurityEventRepository;
import com.lockify.shared.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Security events record aur suspicious activity detect karta hai.
 */
@Service
@RequiredArgsConstructor
public class SecurityEventService {

    public static final String EVENT_SUSPICIOUS_LOGIN = "SUSPICIOUS_LOGIN";
    public static final String EVENT_MULTIPLE_LOCATIONS = "MULTIPLE_LOCATIONS";
    public static final String EVENT_TOKEN_REUSE = "TOKEN_REUSE_ATTACK";
    public static final String SEVERITY_HIGH = "HIGH";
    public static final String SEVERITY_MEDIUM = "MEDIUM";

    private final SecurityEventRepository securityEventRepository;

    @Transactional
    public void recordEvent(User user, String eventType, String severity, String ip, String location, String details) {
        SecurityEvent event = SecurityEvent.builder()
                .user(user)
                .eventType(eventType)
                .severity(severity)
                .ipAddress(ip)
                .location(location)
                .details(details)
                .build();
        securityEventRepository.save(event);
    }

    @Transactional
    public void recordTokenReuseAttack(User user, String familyId) {
        recordEvent(user, EVENT_TOKEN_REUSE, SEVERITY_HIGH, null, null,
                "Refresh token reuse detected, familyId=" + familyId);
    }

    @Transactional
    public void checkSuspiciousLogin(User user, String ip, String location) {
        long recentLogins = securityEventRepository.countByUserIdAndEventTypeAndCreatedAtAfter(
                user.getId(), EVENT_SUSPICIOUS_LOGIN, Instant.now().minus(1, ChronoUnit.HOURS));

        if (recentLogins >= 5) {
            recordEvent(user, EVENT_SUSPICIOUS_LOGIN, SEVERITY_MEDIUM, ip, location,
                    "Bahut saare login attempts last hour me");
        }

        if (location != null) {
            recordEvent(user, EVENT_MULTIPLE_LOCATIONS, SEVERITY_MEDIUM, ip, location,
                    "Login from location: " + location);
        }
    }

    @Transactional(readOnly = true)
    public List<SecurityEventResponse> getAllEvents() {
        return securityEventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SecurityEventResponse> getEventsForUser(Long userId) {
        return securityEventRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SecurityEventResponse toResponse(SecurityEvent event) {
        return SecurityEventResponse.builder()
                .id(event.getId())
                .userId(event.getUser() != null ? event.getUser().getId() : null)
                .eventType(event.getEventType())
                .severity(event.getSeverity())
                .ipAddress(event.getIpAddress())
                .location(event.getLocation())
                .details(event.getDetails())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
