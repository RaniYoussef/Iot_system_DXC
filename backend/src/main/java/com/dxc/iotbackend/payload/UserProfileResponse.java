package com.dxc.iotbackend.payload;

public class UserProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePhoto;
    private String phoneNumber;

    public UserProfileResponse(String username, String firstName, String lastName, String email, String profilePhoto, String phoneNumber ){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getProfilePhoto() { return profilePhoto; }
    public String getPhoneNumber () { return  phoneNumber; }
//    public Boolean  isOauthUser() { return oauthUser; }
}
