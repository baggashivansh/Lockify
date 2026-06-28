package com.lockify.phase1.coreauth.entity;

import com.lockify.shared.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Refresh Token Entity - long-lived token database me store hota hai.
 *
 * Security note: Raw token client ko milta hai, database me sirf SHA-256 hash store karte hain.
 * Agar DB leak ho bhi jaye to tokens directly use nahi ho sakte.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** SHA-256 hash of the actual refresh token */
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    // Phase 7 - Token rotation family tracking
    @Column(name = "family_id")
    private String familyId;

    @Column(name = "replaced_by_hash")
    private String replacedByHash;

    /** Token abhi valid hai ya nahi - expiry aur revoke dono check */
    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }
}
