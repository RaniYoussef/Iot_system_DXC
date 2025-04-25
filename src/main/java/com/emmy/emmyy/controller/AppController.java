package com.emmy.emmyy.controller;

import com.emmy.emmyy.model.User;
import com.emmy.emmyy.payload.SignInRequest;
import com.emmy.emmyy.payload.SignUpRequest;
import com.emmy.emmyy.payload.UpdatePasswordRequest;
import com.emmy.emmyy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private UserService userService;

    // POST /signup
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        String response = userService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }

    // POST /signin
    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        String response = userService.authenticateUser(signInRequest);
        return ResponseEntity.ok(response);
    }

    // GET /profile
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestParam String email) {
        User user = userService.getUserProfile(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    // PUT /profile/password
    @PutMapping("/profile/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        String response = userService.updatePassword(updatePasswordRequest);
        return ResponseEntity.ok(response);
    }
}
