package com.lockify.phase1.coreauth.security.jwt;

import com.lockify.shared.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JWT Service Unit Tests - Module 3 test cases.
 * Bina Spring context ke fast unit tests.
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-minimum-32-characters-long!!");
        properties.setAccessTokenExpiryMinutes(15);
        properties.setRefreshTokenExpiryDays(7);
        properties.setIssuer("lockify-test");

        jwtService = new JwtService(properties, new ObjectMapper());
    }

    @Test
    @DisplayName("Valid token generate aur parse hona chahiye")
    void validToken() {
        String token = jwtService.generateAccessToken(1L, "testuser", Set.of("USER"), Set.of("READ"));
        JwtClaims claims = jwtService.validateAndParse(token);

        assertThat(claims.getUserId()).isEqualTo(1L);
        assertThat(claims.getUsername()).isEqualTo("testuser");
        assertThat(claims.getRoles()).contains("USER");
        assertThat(claims.getPermissions()).contains("READ");
        assertThat(claims.getTokenType()).isEqualTo("access");
        assertThat(claims.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Tampered token pe invalid signature error aana chahiye")
    void tamperedToken() {
        String token = jwtService.generateAccessToken(1L, "testuser", Set.of("USER"), Set.of("READ"));
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThatThrownBy(() -> jwtService.validateAndParse(tampered))
                .hasMessageContaining("signature");
    }

    @Test
    @DisplayName("Invalid format token reject hona chahiye")
    void invalidFormat() {
        assertThatThrownBy(() -> jwtService.validateAndParse("invalid.token"))
                .hasMessageContaining("Invalid JWT format");
    }

    @Test
    @DisplayName("Opaque refresh token unique generate hona chahiye")
    void opaqueRefreshToken() {
        String token1 = jwtService.generateOpaqueRefreshToken();
        String token2 = jwtService.generateOpaqueRefreshToken();

        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.hashToken(token1)).isNotEqualTo(jwtService.hashToken(token2));
    }
}
