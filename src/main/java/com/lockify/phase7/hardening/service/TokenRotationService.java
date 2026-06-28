package com.lockify.phase7.hardening.service;

import com.lockify.phase1.coreauth.entity.RefreshToken;
import com.lockify.phase1.coreauth.repository.RefreshTokenRepository;
import com.lockify.phase1.coreauth.security.jwt.JwtService;
import com.lockify.shared.config.JwtProperties;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Refresh token rotation with families - reuse attack pe poori family revoke.
 */
@Service
@RequiredArgsConstructor
public class TokenRotationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final SecurityEventService securityEventService;

    @Transactional
    public RotationResult rotateRefreshToken(String rawRefreshToken) {
        String tokenHash = jwtService.hashToken(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenException("Invalid refresh token"));

        // Reuse attack - revoked token dubara use hua
        if (stored.isRevoked()) {
            if (stored.getFamilyId() != null) {
                refreshTokenRepository.revokeAllByFamilyId(stored.getFamilyId());
                securityEventService.recordTokenReuseAttack(stored.getUser(), stored.getFamilyId());
            }
            throw new TokenException("Token reuse detect hua - saari sessions revoke, dubara login karo");
        }

        if (!stored.isValid()) {
            throw new TokenException("Refresh token expire ho chuka hai");
        }

        String familyId = stored.getFamilyId() != null ? stored.getFamilyId() : UUID.randomUUID().toString();

        String newRaw = jwtService.generateOpaqueRefreshToken();
        String newHash = jwtService.hashToken(newRaw);

        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        stored.setFamilyId(familyId);
        stored.setReplacedByHash(newHash);
        refreshTokenRepository.save(stored);

        Instant expiry = Instant.now().plusSeconds(
                (long) jwtProperties.getRefreshTokenExpiryDays() * 24 * 60 * 60
        );

        RefreshToken newToken = RefreshToken.builder()
                .tokenHash(newHash)
                .user(stored.getUser())
                .expiresAt(expiry)
                .revoked(false)
                .familyId(familyId)
                .build();
        refreshTokenRepository.save(newToken);

        return new RotationResult(stored.getUser(), newRaw);
    }

    public record RotationResult(User user, String newRefreshToken) {}
}
