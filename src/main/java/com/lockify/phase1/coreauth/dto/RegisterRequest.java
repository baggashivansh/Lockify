package com.lockify.phase1.coreauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registration request DTO - client se aane wala data.
 * Validation yahan hoti hai taaki invalid data service layer tak na pahunche.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username required hai")
    @Size(min = 3, max = 50, message = "Username 3-50 characters ka hona chahiye")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username me sirf letters, numbers aur underscore allowed hain")
    private String username;

    @NotBlank(message = "Email required hai")
    @Email(message = "Valid email format chahiye")
    private String email;

    @NotBlank(message = "Password required hai")
    @Size(min = 8, max = 100, message = "Password minimum 8 characters ka hona chahiye")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password me uppercase, lowercase, number aur special character hona chahiye"
    )
    private String password;
}
