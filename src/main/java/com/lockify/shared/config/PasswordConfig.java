package com.lockify.shared.config;

import com.lockify.shared.security.PepperedPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password encoding - Peppered BCrypt (see PepperedPasswordEncoder for why).
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder(SecurityProperties securityProperties) {
        return new PepperedPasswordEncoder(securityProperties);
    }
}
