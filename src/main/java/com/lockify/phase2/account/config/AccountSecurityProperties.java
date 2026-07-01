package com.lockify.phase2.account.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Phase 2 account security settings - application.yml se bind hoti hain.
 */
@ConfigurationProperties(prefix = "lockify.phase2")
@Getter
@Setter
public class AccountSecurityProperties {

    /** Kitne failed attempts ke baad account lock hoga */
    private int maxFailedAttempts = 5;

    /** Account kitne minutes ke liye locked rahega */
    private int lockDurationMinutes = 30;

    /** Email verification token kitne hours valid rahega */
    private int emailVerificationHours = 24;

    /** Password reset token kitne hours valid rahega */
    private int passwordResetHours = 1;

    /** Kitne purane passwords reuse nahi kar sakte */
    private int passwordHistoryCount = 5;

    /** Password kitne din baad expire hoga */
    private int passwordExpiryDays = 90;
}
