package com.emmy.emmyy.payload;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
