package com.lockify.shared.security;

import com.lockify.shared.config.SecurityProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Peppered BCrypt Password Encoder
 *
 * FLOW:
 * 1. Client password: "MyPass@123"
 * 2. Server pepper add: "MyPass@123" + pepper (HMAC-style mixing)
 * 3. BCrypt hash with auto-salt
 *
 * KYUN PEPPER?
 * - Rainbow tables useless even if DB stolen (pepper not in DB)
 * - Extra layer beyond BCrypt salt
 * - Rotate pepper via pepper_version in user_credentials
 */
public class PepperedPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate;
    private final String pepper;

    public PepperedPasswordEncoder(SecurityProperties properties) {
        this.delegate = new BCryptPasswordEncoder(properties.getBcryptStrength());
        this.pepper = properties.getPepper() != null ? properties.getPepper() : "";
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(applyPepper(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegate.matches(applyPepper(rawPassword), encodedPassword);
    }

    private String applyPepper(CharSequence rawPassword) {
        if (pepper.isBlank()) {
            return rawPassword.toString();
        }
        // Pepper end me append - simple aur effective
        return rawPassword.toString() + pepper;
    }
}
