package com.example.demo;

import jakarta.validation.constraints.*;

public class UserDto {
    @NotBlank(message = "Username is required")
    private String username;

    //@Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$",
            message = "Email format is invalid"
    )
    private String email;

    @NotBlank(message = "Password is required")
    //@Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters, include at least 1 uppercase, 1 lowercase, 1 number, and 1 special Character"
    )
    private String password;

    // Default constructor (required for JSON deserialization)
    public UserDto() {}

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
