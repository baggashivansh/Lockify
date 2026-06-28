package com.lockify.phase4.enterprise.audit.service;

import com.lockify.phase4.enterprise.audit.entity.AuditLog;
import com.lockify.phase4.enterprise.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Audit logging service - compliance ke liye actions record karte hain.
 * Kabhi bhi password, token, secret log mat karo.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "token", "refreshToken", "accessToken", "secret",
            "totpSecret", "code", "otp", "authorization"
    );
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            "(?i)(password|token|secret|otp|bearer)\\s*[:=]\\s*\\S+"
    );

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(Long userId, String action, String resource, String details) {
        String safeDetails = sanitizeDetails(details);
        HttpServletRequest request = currentRequest();

        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .action(action)
                .resource(resource)
                .details(safeDetails)
                .ipAddress(request != null ? request.getRemoteAddr() : null)
                .userAgent(request != null ? truncate(request.getHeader("User-Agent"), 500) : null)
                .build();

        auditLogRepository.save(entry);
    }

    /** Sensitive fields ko mask karke details safe banao */
    public String sanitizeDetails(String details) {
        if (details == null || details.isBlank()) {
            return details;
        }
        String sanitized = details;
        for (String key : SENSITIVE_KEYS) {
            sanitized = sanitized.replaceAll("(?i)\"" + key + "\"\\s*:\\s*\"[^\"]*\"", "\"" + key + "\":\"[REDACTED]\"");
            sanitized = sanitized.replaceAll("(?i)" + key + "\\s*=\\s*\\S+", key + "=[REDACTED]");
        }
        return SENSITIVE_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED]");
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String truncate(String value, int maxLen) {
        if (value == null || value.length() <= maxLen) {
            return value;
        }
        return value.substring(0, maxLen);
    }
}
