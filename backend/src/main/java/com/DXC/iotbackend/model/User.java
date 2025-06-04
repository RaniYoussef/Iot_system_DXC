//package com.dxc.iotbackend.model;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//import lombok.Data;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//@JsonIgnoreProperties(ignoreUnknown = false)
//@Data
//@Entity
//@Table(name = "users")
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    @NotBlank(message = "First name is required")
//    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
//    private String firstName;
//
//    @Column(nullable = false)
//    @NotBlank(message = "Last name is required")
//    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
//    private String lastName;
//
//    @Column(unique = true,nullable = false)
//    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format")
//    @NotBlank(message = "Email is required")
//    private String email;
//
//
//    @Column(nullable = false, length =64) //encoded password so 64 as max length
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+={\\[\\]}-]).{6,}$",
//            message = "Password must contain at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
//    )
//    @NotBlank(message = "Password is required")
//    @Size(min = 6, message = "Password must be at least 6 characters")
//    private String password;
//
//
//}
