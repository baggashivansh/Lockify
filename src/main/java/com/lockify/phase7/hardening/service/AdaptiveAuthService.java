package com.lockify.phase7.hardening.service;

import com.lockify.shared.domain.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Adaptive auth - naya device ya naya country pe MFA require flag set karta hai.
 */
@Service
@RequiredArgsConstructor
public class AdaptiveAuthService {

    private final DeviceFingerprintService deviceFingerprintService;
    private final SecurityEventService securityEventService;

    public AdaptiveAuthDecision evaluate(User user, String fingerprint, String ip, String country) {
        boolean knownDevice = deviceFingerprintService.isKnownDevice(user.getId(), fingerprint);
        boolean requireMfa = !knownDevice;

        if (!knownDevice) {
            securityEventService.recordEvent(user, SecurityEventService.EVENT_SUSPICIOUS_LOGIN,
                    SecurityEventService.SEVERITY_MEDIUM, ip, country,
                    "Naya device detect hua - MFA recommend");
        }

        return AdaptiveAuthDecision.builder()
                .requireMfa(requireMfa)
                .knownDevice(knownDevice)
                .reason(knownDevice ? "Known device" : "New device detected")
                .build();
    }

    @Data
    @Builder
    public static class AdaptiveAuthDecision {
        private boolean requireMfa;
        private boolean knownDevice;
        private String reason;
    }
}
