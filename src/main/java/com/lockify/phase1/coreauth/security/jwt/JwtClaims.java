package com.lockify.phase1.coreauth.security.jwt;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;

/**
 * Parsed JWT claims - filter chain me authentication object banane ke liye use hota hai.
 */
@Getter
@Builder
public class JwtClaims {

    private Long userId;
    private String username;
    private Set<String> roles;
    private Set<String> permissions;
    private String tokenType;
    private Instant expiresAt;
}
