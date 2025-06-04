package com.DXC.iotbackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "userssss")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_issued_at")
    private Long tokenIssuedAt; // store as epoch milliseconds


    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;


    private String username;
    private String password;
    private String email;
    private String role; // "USER" or "ADMIN"
    private String phoneNumber;


    private String firstName;
    private String lastName;
//    @Column(name = "oauth_user")
//    private Boolean  oauthUser = false;



    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String profilePhoto;
    // Constructors
    public UserEntity() {}

    public UserEntity(String username, String password, String email,String role, String firstName, String lastName,String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role=role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;

    }

//    public UserEntity(String username, String password, String email,String role, String firstName, String lastName, String profilePhoto) {
//        this.username = username;
//        this.password = password;
//        this.email = email;
//        this.role=role;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.profilePhoto = profilePhoto ;
//
//    }

    // Getters & Setters
    public Long getId() { return id; }


    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }



    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }


    public Long getTokenIssuedAt() {
        return tokenIssuedAt;
    }

    public void setTokenIssuedAt(Long tokenIssuedAt) {
        this.tokenIssuedAt = tokenIssuedAt;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }



//    public Boolean  isOAuthUser() {
//        return oauthUser;
//    }
//
//    public void setOAuthUser(Boolean  oauthUser) {
//        this.oauthUser = oauthUser;
//    }
}
