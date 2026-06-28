package com.lockify.phase1.coreauth.controller;

import com.lockify.phase1.coreauth.dto.LoginRequest;
import com.lockify.phase1.coreauth.dto.RefreshTokenRequest;
import com.lockify.phase1.coreauth.dto.RegisterRequest;
import com.lockify.phase1.coreauth.dto.AuthResponse;
import com.lockify.phase1.coreauth.dto.UserResponse;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import com.lockify.phase1.coreauth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller - public authentication APIs.
 *
 * Yeh layer sirf HTTP request/response handle karti hai.
 * Business logic AuthService me hai - controller me logic mat likho.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Module 1 - POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Module 2 & 3 - POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    /** Module 4 - POST /api/auth/refresh */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    /** Protected - current user profile */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal LockifyUserDetails userDetails) {
        return ResponseEntity.ok(authService.getCurrentUser(userDetails));
    }
}
