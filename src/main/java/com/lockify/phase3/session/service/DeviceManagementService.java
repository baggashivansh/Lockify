package com.lockify.phase3.session.service;

import com.lockify.phase3.session.dto.DeviceResponse;
import com.lockify.phase3.session.entity.TrustedDevice;
import com.lockify.phase3.session.repository.TrustedDeviceRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.exception.LockifyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Device Management Service - trusted devices manage karta hai.
 */
@Service
@RequiredArgsConstructor
public class DeviceManagementService {

    private final TrustedDeviceRepository trustedDeviceRepository;

    /** Device ko trusted mark karo (create ya update) */
    @Transactional
    public DeviceResponse trustDevice(User user, String deviceId, HttpServletRequest request) {
        TrustedDevice device = trustedDeviceRepository.findByUserAndDeviceId(user, deviceId)
                .orElseGet(() -> TrustedDevice.builder()
                        .user(user)
                        .deviceId(deviceId)
                        .build());

        device.setDeviceName(request.getHeader("X-Device-Name"));
        device.setFingerprint(request.getHeader("X-Device-Fingerprint"));
        device.setTrusted(true);
        device.setLastUsedAt(Instant.now());

        return toResponse(trustedDeviceRepository.save(device));
    }

    /** User ke saare devices list karo */
    @Transactional(readOnly = true)
    public List<DeviceResponse> listDevices(User user) {
        return trustedDeviceRepository.findByUserOrderByLastUsedAtDesc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Device trust revoke karo aur record delete karo */
    @Transactional
    public void revokeDevice(User user, String deviceId) {
        TrustedDevice device = trustedDeviceRepository.findByUserAndDeviceId(user, deviceId)
                .orElseThrow(() -> new LockifyException("Device nahi mila", HttpStatus.NOT_FOUND));

        trustedDeviceRepository.delete(device);
    }

    private DeviceResponse toResponse(TrustedDevice device) {
        return DeviceResponse.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .trusted(device.isTrusted())
                .lastUsedAt(device.getLastUsedAt())
                .createdAt(device.getCreatedAt())
                .build();
    }
}
