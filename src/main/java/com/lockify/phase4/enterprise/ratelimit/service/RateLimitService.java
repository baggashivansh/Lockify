package com.lockify.phase4.enterprise.ratelimit.service;

import com.lockify.phase4.enterprise.ratelimit.config.RateLimitProperties;
import com.lockify.phase4.enterprise.ratelimit.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based sliding window rate limiter.
 * Har request ek sorted set entry hai - window se bahar wale entries clean ho jate hain.
 */
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String KEY_PREFIX = "lockify:ratelimit:";

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;

    public void checkRateLimit(String clientKey, String endpoint) {
        if (!properties.isEnabled()) {
            return;
        }

        String redisKey = KEY_PREFIX + endpoint + ":" + clientKey;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - TimeUnit.MINUTES.toMillis(1);

        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
        zSet.removeRangeByScore(redisKey, 0, windowStart);
        Long count = zSet.zCard(redisKey);

        if (count != null && count >= properties.getRequestsPerMinute()) {
            throw new RateLimitExceededException("Bahut zyada requests - thodi der baad try karo");
        }

        zSet.add(redisKey, UUID.randomUUID().toString(), now);
        redisTemplate.expire(redisKey, 2, TimeUnit.MINUTES);
    }
}
