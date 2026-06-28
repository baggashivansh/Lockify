package com.lockify.phase4.enterprise.ratelimit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Rate limit settings - auth endpoints pe brute force se bachne ke liye.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "lockify.phase4.ratelimit")
public class RateLimitProperties {

    private boolean enabled = true;

    /** Default: 10 requests per minute per IP */
    private int requestsPerMinute = 10;

    private List<String> authEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/account/forgot-password"
    );
}
