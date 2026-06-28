package com.lockify.phase1.coreauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login request - identifier email YA username ho sakta hai.
 * Ek hi field se dono support karte hain production apps me common pattern hai.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email ya username required hai")
    private String identifier;

    @NotBlank(message = "Password required hai")
    private String password;
}
