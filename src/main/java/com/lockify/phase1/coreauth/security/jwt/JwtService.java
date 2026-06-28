package com.lockify.phase1.coreauth.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lockify.shared.config.JwtProperties;
import com.lockify.shared.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual JWT Implementation - koi external JWT library use nahi ki.
 *
 * JWT structure: header.payload.signature
 * - Header: algorithm info (HS256)
 * - Payload: claims (sub, roles, exp, iat, iss)
 * - Signature: HMAC-SHA256(header.payload, secret)
 *
 * Yeh approach samajhne ke liye best hai - production me bhi valid hai agar correctly implement ho.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String ALGORITHM = "HS256";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    /**
     * Access token generate karo - short lived (default 15 min)
     */
    public String generateAccessToken(Long userId, String username, Set<String> roles, Set<String> permissions) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenExpiryMinutes() * 60L);

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", String.valueOf(userId));
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("type", "access");

        return buildToken(claims, now, expiry);
    }

    /**
     * Token validate karo aur claims return karo.
     * Invalid signature, expiry, ya tampered token pe exception throw hoga.
     */
    public JwtClaims validateAndParse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new TokenException("Invalid JWT format - 3 parts hone chahiye");
        }

        String headerB64 = parts[0];
        String payloadB64 = parts[1];
        String signatureB64 = parts[2];

        // Signature verify karo - tampering detect hoti hai
        String expectedSignature = sign(headerB64 + "." + payloadB64);
        if (!constantTimeEquals(expectedSignature, signatureB64)) {
            throw new TokenException("Invalid token signature - token tampered ho sakta hai");
        }

        Map<String, Object> payload = decodeJson(base64UrlDecode(payloadB64));
        long exp = ((Number) payload.get("exp")).longValue();
        if (Instant.now().getEpochSecond() > exp) {
            throw new TokenException("Token expire ho chuka hai - refresh token use karo");
        }

        String issuer = (String) payload.get("iss");
        if (!jwtProperties.getIssuer().equals(issuer)) {
            throw new TokenException("Invalid token issuer");
        }

        @SuppressWarnings("unchecked")
        Set<String> roles = ((java.util.List<String>) payload.getOrDefault("roles", java.util.List.of()))
                .stream().collect(Collectors.toSet());
        @SuppressWarnings("unchecked")
        Set<String> permissions = ((java.util.List<String>) payload.getOrDefault("permissions", java.util.List.of()))
                .stream().collect(Collectors.toSet());

        return JwtClaims.builder()
                .userId(Long.parseLong((String) payload.get("sub")))
                .username((String) payload.get("username"))
                .roles(roles)
                .permissions(permissions)
                .tokenType((String) payload.get("type"))
                .expiresAt(Instant.ofEpochSecond(exp))
                .build();
    }

    public long getAccessTokenExpirySeconds() {
        return jwtProperties.getAccessTokenExpiryMinutes() * 60L;
    }

    /**
     * Refresh token ke liye cryptographically secure random string generate karo.
     * Yeh JWT nahi hai - opaque token hai jo DB me hash store hota hai.
     */
    public String generateOpaqueRefreshToken() {
        byte[] randomBytes = new byte[32];
        new java.security.SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Refresh token ka SHA-256 hash - DB me yeh store hota hai, raw token nahi.
     */
    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm available nahi hai", e);
        }
    }

    private String buildToken(Map<String, Object> claims, Instant issuedAt, Instant expiry) {
        Map<String, Object> header = Map.of("alg", ALGORITHM, "typ", "JWT");

        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", expiry.getEpochSecond());
        claims.put("iss", jwtProperties.getIssuer());

        String headerB64 = base64UrlEncode(encodeJson(header));
        String payloadB64 = base64UrlEncode(encodeJson(claims));
        String signature = sign(headerB64 + "." + payloadB64);

        return headerB64 + "." + payloadB64 + "." + signature;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(
                    jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(key);
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("JWT signing fail ho gayi", e);
        }
    }

    /** Timing attack se bachne ke liye constant-time comparison */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private String base64UrlEncode(String data) {
        return base64UrlEncode(data.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }

    private String encodeJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON encode fail", e);
        }
    }

    private Map<String, Object> decodeJson(byte[] json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new TokenException("Token payload parse nahi ho paya");
        }
    }
}
