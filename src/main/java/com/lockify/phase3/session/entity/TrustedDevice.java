package com.lockify.phase3.session.entity;

import com.lockify.shared.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Trusted Device Entity - user ke trusted devices track karta hai.
 */
@Entity
@Table(
        name = "trusted_devices",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "device_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrustedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(length = 255)
    private String fingerprint;

    @Column(nullable = false)
    @Builder.Default
    private boolean trusted = true;

    @Column(name = "last_used_at", nullable = false)
    @Builder.Default
    private Instant lastUsedAt = Instant.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
