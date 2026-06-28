package com.lockify.phase3.session.controller;

import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import com.lockify.phase2.account.dto.MessageResponse;
import com.lockify.phase3.session.dto.DeviceResponse;
import com.lockify.phase3.session.dto.SessionResponse;
import com.lockify.phase3.session.service.DeviceManagementService;
import com.lockify.phase3.session.service.LogoutService;
import com.lockify.phase3.session.service.SessionTrackingService;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Session Controller - active sessions aur trusted devices manage karne ke APIs.
 */
@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionTrackingService sessionTrackingService;
    private final LogoutService logoutService;
    private final DeviceManagementService deviceManagementService;
    private final UserRepository userRepository;

    /** Module 11 - GET /api/sessions */
    @GetMapping("/api/sessions")
    public ResponseEntity<List<SessionResponse>> listSessions(@AuthenticationPrincipal LockifyUserDetails userDetails) {
        User user = loadUser(userDetails.getId());
        return ResponseEntity.ok(sessionTrackingService.listActiveSessions(user));
    }

    /** Module 11 - DELETE /api/sessions/{sessionId} */
    @DeleteMapping("/api/sessions/{sessionId}")
    public ResponseEntity<MessageResponse> revokeSession(
            @AuthenticationPrincipal LockifyUserDetails userDetails,
            @PathVariable String sessionId) {
        User user = loadUser(userDetails.getId());
        sessionTrackingService.revokeSession(user, sessionId);
        return ResponseEntity.ok(MessageResponse.of("Session revoke ho gayi"));
    }

    /** Module 12 - POST /api/sessions/logout-all */
    @PostMapping("/api/sessions/logout-all")
    public ResponseEntity<MessageResponse> logoutAll(@AuthenticationPrincipal LockifyUserDetails userDetails) {
        return ResponseEntity.ok(logoutService.logoutAll(userDetails.getId()));
    }

    /** Module 13 - GET /api/devices */
    @GetMapping("/api/devices")
    public ResponseEntity<List<DeviceResponse>> listDevices(@AuthenticationPrincipal LockifyUserDetails userDetails) {
        User user = loadUser(userDetails.getId());
        return ResponseEntity.ok(deviceManagementService.listDevices(user));
    }

    /** Module 13 - POST /api/devices/{deviceId}/trust */
    @PostMapping("/api/devices/{deviceId}/trust")
    public ResponseEntity<DeviceResponse> trustDevice(
            @AuthenticationPrincipal LockifyUserDetails userDetails,
            @PathVariable String deviceId,
            HttpServletRequest request) {
        User user = loadUser(userDetails.getId());
        return ResponseEntity.ok(deviceManagementService.trustDevice(user, deviceId, request));
    }

    /** Module 13 - DELETE /api/devices/{deviceId} */
    @DeleteMapping("/api/devices/{deviceId}")
    public ResponseEntity<MessageResponse> revokeDevice(
            @AuthenticationPrincipal LockifyUserDetails userDetails,
            @PathVariable String deviceId) {
        User user = loadUser(userDetails.getId());
        deviceManagementService.revokeDevice(user, deviceId);
        return ResponseEntity.ok(MessageResponse.of("Device revoke ho gaya"));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));
    }
}
