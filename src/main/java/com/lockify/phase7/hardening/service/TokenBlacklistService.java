package com.lockify.phase7.hardening.service;

import com.lockify.phase1.coreauth.security.jwt.JwtService;
import com.lockify.shared.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Access token blacklist - logout ya compromise pe token invalidate.
 * Redis me hash store hota hai, TTL = token remaining lifetime.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "lockify:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public void blacklist(String accessToken, Instant expiresAt) {
        String key = KEY_PREFIX + jwtService.hashToken(accessToken);
        long ttlSeconds = Math.max(1, expiresAt.getEpochSecond() - Instant.now().getEpochSecond());
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
    }

    public void blacklist(String accessToken) {
        long ttl = jwtProperties.getAccessTokenExpiryMinutes() * 60L;
        String key = KEY_PREFIX + jwtService.hashToken(accessToken);
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(ttl));
    }

    public boolean isBlacklisted(String accessToken) {
        String key = KEY_PREFIX + jwtService.hashToken(accessToken);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
