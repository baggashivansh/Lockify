package com.lockify.phase3.session.entity;

import com.lockify.shared.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * User Session Entity - har login ka device/IP/browser tracking record.
 */
@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_id", nullable = false, unique = true, length = 64)
    private String sessionId;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(length = 100)
    private String browser;

    @Column(length = 100)
    private String os;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "login_at", nullable = false)
    @Builder.Default
    private Instant loginAt = Instant.now();

    @Column(name = "last_activity", nullable = false)
    @Builder.Default
    private Instant lastActivity = Instant.now();

    @Column(name = "logout_at")
    private Instant logoutAt;
}
