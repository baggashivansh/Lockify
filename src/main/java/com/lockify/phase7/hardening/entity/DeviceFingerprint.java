package com.lockify.phase7.hardening.entity;

import com.lockify.shared.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** Device fingerprint - new device detect karne ke liye */
@Entity
@Table(name = "device_fingerprints", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "fingerprint_hash"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceFingerprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fingerprint_hash", nullable = false)
    private String fingerprintHash;

    @Column(length = 100)
    private String browser;

    @Column(length = 100)
    private String os;

    @Column(name = "screen_res", length = 50)
    private String screenRes;

    @Column(length = 50)
    private String timezone;

    @CreationTimestamp
    @Column(name = "first_seen", nullable = false, updatable = false)
    private Instant firstSeen;

    @Column(name = "last_seen", nullable = false)
    private Instant lastSeen;
}
