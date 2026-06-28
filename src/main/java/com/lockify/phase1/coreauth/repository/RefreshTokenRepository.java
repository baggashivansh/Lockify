package com.lockify.phase1.coreauth.repository;

import com.lockify.phase1.coreauth.entity.RefreshToken;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByFamilyId(String familyId);

    /** Logout everywhere - user ke saare refresh tokens revoke kar do */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.user = :user AND rt.revoked = false")
    void revokeAllByUser(User user);

    /** Phase 7 - reuse attack pe poori token family revoke */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.familyId = :familyId AND rt.revoked = false")
    void revokeAllByFamilyId(String familyId);
}
