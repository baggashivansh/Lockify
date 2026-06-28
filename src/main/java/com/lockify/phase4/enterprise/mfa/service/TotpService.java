package com.lockify.phase4.enterprise.mfa.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Manual TOTP implementation - RFC 6238, HMAC-SHA1.
 * Koi external TOTP library use nahi ki - learning ke liye best hai.
 */
@Service
public class TotpService {

    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    private final SecureRandom secureRandom = new SecureRandom();

    /** Naya Base32-style secret generate karo (URL-safe Base64) */
    public String generateSecret() {
        byte[] bytes = new byte[20];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** otpauth:// URI - authenticator apps isse scan karti hain */
    public String buildOtpAuthUri(String issuer, String accountName, String secret) {
        return "otpauth://totp/" + issuer + ":" + accountName
                + "?secret=" + secret + "&issuer=" + issuer + "&digits=" + CODE_DIGITS;
    }

    /** Current time window ka 6-digit TOTP code */
    public String generateCode(String secret) {
        return generateCode(secret, Instant.now().getEpochSecond() / TIME_STEP_SECONDS);
    }

    /** User ka code verify karo - ±1 window tolerance for clock skew */
    public boolean verifyCode(String secret, String code) {
        long counter = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;
        for (long offset = -1; offset <= 1; offset++) {
            if (generateCode(secret, counter + offset).equals(code)) {
                return true;
            }
        }
        return false;
    }

    private String generateCode(String secret, long counter) {
        byte[] key = Base64.getUrlDecoder().decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(counter).array();

        byte[] hash = hmacSha1(key, data);
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);
        int otp = binary % (int) Math.pow(10, CODE_DIGITS);
        return String.format("%0" + CODE_DIGITS + "d", otp);
    }

    private byte[] hmacSha1(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("TOTP HMAC fail ho gaya", e);
        }
    }
}
