package com.lockify.shared.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * UserCredential - password aur lock state yahan store hota hai.
 *
 * KYUN ALAG TABLE?
 * - Identity (User) aur secrets (Credential) alag = breach containment
 * - Agar profiles leak hon, passwords alag table me safe
 * - Different access policies DB level pe lag sakti hain
 *
 * KABHI LOG MAT KARO: password_hash field
 */
@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** BCrypt hash - plain password kabhi store nahi hota */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "hash_algorithm", nullable = false, length = 20)
    @Builder.Default
    private String hashAlgorithm = "BCRYPT";

    @Column(name = "hash_strength", nullable = false)
    @Builder.Default
    private int hashStrength = 12;

    /** Pepper rotate karne pe version bump - purane hashes re-hash kar sakte ho */
    @Column(name = "pepper_version", nullable = false)
    @Builder.Default
    private int pepperVersion = 1;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "password_expires_at")
    private Instant passwordExpiresAt;

    @Column(name = "mfa_required", nullable = false)
    @Builder.Default
    private boolean mfaRequired = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public boolean isTemporarilyLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public boolean isPasswordExpired() {
        return passwordExpiresAt != null && Instant.now().isAfter(passwordExpiresAt);
    }
}
