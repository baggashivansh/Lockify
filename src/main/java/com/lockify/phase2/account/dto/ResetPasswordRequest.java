package com.lockify.phase2.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token required hai")
    private String token;

    @NotBlank(message = "Password required hai")
    @Size(min = 8, max = 100, message = "Password minimum 8 characters ka hona chahiye")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password me uppercase, lowercase, number aur special character hona chahiye"
    )
    private String newPassword;
}
