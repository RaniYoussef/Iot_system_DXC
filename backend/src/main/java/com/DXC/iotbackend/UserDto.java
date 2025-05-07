package com.DXC.iotbackend;

import jakarta.validation.constraints.*;

public class UserDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s]+$",
            message = "First name must contain only letters and spaces"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s]+$",
            message = "Last name must contain only letters and spaces"
    )
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9]+$",
            message = "Username must contain only letters and numbers (no spaces or special characters)"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$",
            message = "Email format is invalid"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters, include at least 1 uppercase, 1 lowercase, 1 number, and 1 special character"
    )
    private String password;

    public UserDto() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
