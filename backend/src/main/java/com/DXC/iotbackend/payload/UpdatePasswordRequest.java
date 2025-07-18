package com.dxc.iotbackend.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must contain at least 1 lowercase, 1 uppercase, 1 digit, 1 special character and be at least 6 characters long"
    )
    private String newPassword;
}

