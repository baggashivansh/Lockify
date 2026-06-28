package com.lockify.shared.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - SIRF identity aur account status.
 *
 * IMPORTANT: Password yahan NAHI hai - user_credentials table me hai.
 * Yeh separation enterprise IAM ka standard pattern hai.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /** Permanent admin lock - temporary lock user_credentials.locked_until me hai */
    @Column(name = "account_locked", nullable = false)
    @Builder.Default
    private boolean accountLocked = false;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserCredential credential;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /** Credential table se password hash - convenience method */
    public String getPasswordHash() {
        return credential != null ? credential.getPasswordHash() : null;
    }

    public boolean isTemporarilyLocked() {
        return credential != null && credential.isTemporarilyLocked();
    }

    public int getFailedLoginAttempts() {
        return credential != null ? credential.getFailedLoginAttempts() : 0;
    }

    public Instant getLockedUntil() {
        return credential != null ? credential.getLockedUntil() : null;
    }

    public Instant getPasswordChangedAt() {
        return credential != null ? credential.getPasswordChangedAt() : null;
    }

    public Instant getPasswordExpiresAt() {
        return credential != null ? credential.getPasswordExpiresAt() : null;
    }
}
