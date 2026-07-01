package com.lockify.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security properties - pepper aur hash settings.
 * PEPPER = server-side secret jo password me mix hota hai BCrypt se pehle.
 * DB leak hone pe bhi attacker ko pepper chahiye hoga brute force ke liye.
 */
@ConfigurationProperties(prefix = "lockify.security")
@Getter
@Setter
public class SecurityProperties {

    /**
     * Application-level pepper - env se aata hai, KABHI commit mat karo.
     * Empty pepper = dev mode (BCrypt only). Production me zaroor set karo.
     */
    private String pepper = "";

    private int bcryptStrength = 12;
}
