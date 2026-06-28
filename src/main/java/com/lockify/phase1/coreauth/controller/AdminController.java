package com.lockify.phase1.coreauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin Controller - RBAC demo endpoints (Module 6).
 *
 * @PreAuthorize se method-level security - role/permission check hota hai
 * JWT filter ke baad yeh annotations enforce hote hain.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /** Sirf ADMIN ya SUPER_ADMIN access kar sakte hain */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of(
                "message", "Admin dashboard - tumhare paas elevated access hai",
                "status", "authorized"
        ));
    }

    /** Permission-based access - CREATE permission chahiye */
    @GetMapping("/users/create-permission-test")
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<Map<String, String>> createPermissionTest() {
        return ResponseEntity.ok(Map.of(
                "message", "CREATE permission verified",
                "status", "authorized"
        ));
    }

    /** Sirf SUPER_ADMIN */
    @GetMapping("/super")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> superAdminOnly() {
        return ResponseEntity.ok(Map.of(
                "message", "Super Admin area - full system access",
                "status", "authorized"
        ));
    }
}
