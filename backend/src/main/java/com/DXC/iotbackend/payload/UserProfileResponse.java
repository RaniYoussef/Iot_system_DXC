package com.DXC.iotbackend.payload;

public class UserProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    // Constructor
    public UserProfileResponse(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
}
