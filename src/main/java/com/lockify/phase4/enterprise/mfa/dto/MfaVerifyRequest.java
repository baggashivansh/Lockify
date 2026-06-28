package com.lockify.phase4.enterprise.mfa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MfaVerifyRequest {

    @NotBlank(message = "Code required hai")
    private String code;

    /** TOTP ya EMAIL - default TOTP */
    private String type = "TOTP";
}
