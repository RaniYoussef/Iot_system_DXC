package com.DXC.iotbackend.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$",
            message = "Email format is invalid"
    )
    private String email;

    @NotBlank(message = "Email is required")
    private String password;

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
