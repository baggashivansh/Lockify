package com.lockify.phase1.coreauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * User-level protected endpoints - koi bhi authenticated user access kar sakta hai.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> profile() {
        return ResponseEntity.ok(Map.of(
                "message", "User profile area - authenticated access",
                "status", "authorized"
        ));
    }

    /** READ permission wale users ke liye */
    @GetMapping("/read-test")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<Map<String, String>> readTest() {
        return ResponseEntity.ok(Map.of(
                "message", "READ permission verified",
                "status", "authorized"
        ));
    }
}
