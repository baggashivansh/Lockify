package com.lockify.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration properties - application.yml se bind hoti hain.
 * Production me JWT_SECRET environment variable se set karo.
 */
@ConfigurationProperties(prefix = "lockify.security.jwt")
@Getter
@Setter
public class JwtProperties {

    /** HMAC signing ke liye secret key - minimum 32 characters */
    private String secret;

    /** Access token kitne minutes valid rahega (default 15) */
    private int accessTokenExpiryMinutes;

    /** Refresh token kitne din valid rahega (default 7) */
    private int refreshTokenExpiryDays;

    /** Token issuer claim - identify karne ke liye kaunsa system ne issue kiya */
    private String issuer;
}
