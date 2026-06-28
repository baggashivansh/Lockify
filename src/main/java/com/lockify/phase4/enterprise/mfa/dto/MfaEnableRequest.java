package com.lockify.phase4.enterprise.mfa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MfaEnableRequest {

    @NotBlank(message = "TOTP code required hai enable karne ke liye")
    private String code;

    /** Email OTP bhi enable karna hai? */
    private boolean enableEmailOtp;
}
