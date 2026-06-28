package com.lockify.phase7.hardening.service;

import com.lockify.phase1.coreauth.security.jwt.JwtService;
import com.lockify.phase7.hardening.entity.DeviceFingerprint;
import com.lockify.phase7.hardening.repository.DeviceFingerprintRepository;
import com.lockify.shared.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Device fingerprint track karta hai - naya device detect karne ke liye.
 */
@Service
@RequiredArgsConstructor
public class DeviceFingerprintService {

    private final DeviceFingerprintRepository deviceFingerprintRepository;
    private final JwtService jwtService;

    @Transactional
    public boolean registerOrRecognize(User user, String rawFingerprint, String browser, String os,
                                       String screenRes, String timezone) {
        String hash = jwtService.hashToken(rawFingerprint);
        return deviceFingerprintRepository.findByUserIdAndFingerprintHash(user.getId(), hash)
                .map(existing -> {
                    existing.setLastSeen(Instant.now());
                    deviceFingerprintRepository.save(existing);
                    return true;
                })
                .orElseGet(() -> {
                    DeviceFingerprint fp = DeviceFingerprint.builder()
                            .user(user)
                            .fingerprintHash(hash)
                            .browser(browser)
                            .os(os)
                            .screenRes(screenRes)
                            .timezone(timezone)
                            .lastSeen(Instant.now())
                            .build();
                    deviceFingerprintRepository.save(fp);
                    return false;
                });
    }

    @Transactional(readOnly = true)
    public boolean isKnownDevice(Long userId, String rawFingerprint) {
        String hash = jwtService.hashToken(rawFingerprint);
        return deviceFingerprintRepository.findByUserIdAndFingerprintHash(userId, hash).isPresent();
    }
}
