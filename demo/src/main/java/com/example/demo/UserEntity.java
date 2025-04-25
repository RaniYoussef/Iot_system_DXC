package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "userssss")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String role; // "USER" or "ADMIN"

    // Constructors
    public UserEntity() {}

    public UserEntity(String username, String password, String email,String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role=role;

    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
