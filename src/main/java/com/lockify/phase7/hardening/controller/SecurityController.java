package com.lockify.phase7.hardening.controller;

import com.lockify.phase7.hardening.dto.SecurityEventResponse;
import com.lockify.phase7.hardening.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityEventService securityEventService;

    @GetMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SecurityEventResponse>> getEvents() {
        return ResponseEntity.ok(securityEventService.getAllEvents());
    }
}
