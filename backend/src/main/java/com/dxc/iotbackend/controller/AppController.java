package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.model.User;
import com.dxc.iotbackend.payload.SignInRequest;
import com.dxc.iotbackend.payload.SignUpRequest;
import com.dxc.iotbackend.payload.UpdatePasswordRequest;
import com.dxc.iotbackend.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular dev server
@RequestMapping("/api")
public class AppController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody JsonNode jsonNode) {
        Set<String> allowedFields = Set.of("firstName", "lastName", "email", "password");

        // 1. Check for unexpected (extra) fields
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            if (!allowedFields.contains(field)) {
                return ResponseEntity
                        .badRequest()
                        .body("Unexpected field: " + field);
            }
        }

        // 2. Check for missing required fields
        for (String requiredField : allowedFields) {
            if (!jsonNode.has(requiredField) || jsonNode.get(requiredField).asText().isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Missing or blank required field: " + requiredField);
            }
        }

        // 3. Map to DTO of signupRequest
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequest signUpRequest = objectMapper.convertValue(jsonNode, SignUpRequest.class);

        // 4. Continue logic
        String response = userService.registerUser(signUpRequest);
        if (response.equals("Email is already in use.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.ok(response);
    }





    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody JsonNode jsonNode) {
        Set<String> allowedFields = Set.of("email", "password");

        // 1. Check for unexpected fields
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            if (!allowedFields.contains(field)) {
                return ResponseEntity
                        .badRequest()
                        .body("Unexpected field: " + field);
            }
        }

        // 2. Check for missing or blank required fields
        for (String requiredField : allowedFields) {
            if (!jsonNode.has(requiredField) || jsonNode.get(requiredField).asText().isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Missing or blank required field: " + requiredField);
            }
        }

        // 3. Map to DTO
        ObjectMapper objectMapper = new ObjectMapper();
        SignInRequest signInRequest = objectMapper.convertValue(jsonNode, SignInRequest.class);

        // 4. Authenticate
        String response = userService.authenticateUser(signInRequest);

        if ("Signed in successfully!".equals(response)) {
            return ResponseEntity.ok(response); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }
    }



    // GET /profile
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.getUserProfile(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/profile/password")
    public ResponseEntity<String> updatePassword(@RequestBody JsonNode jsonNode) {
        Set<String> allowedFields = Set.of("email", "newPassword");

        // 1. Unexpected fields
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            if (!allowedFields.contains(field)) {
                return ResponseEntity
                        .badRequest()
                        .body("Unexpected field: " + field);
            }
        }

        // 2. Missing or blank required fields
        for (String requiredField : allowedFields) {
            if (!jsonNode.has(requiredField) || jsonNode.get(requiredField).asText().isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Missing or blank required field: " + requiredField);
            }
        }

        // 3. Map to DTO
        ObjectMapper objectMapper = new ObjectMapper();
        UpdatePasswordRequest updatePasswordRequest = objectMapper.convertValue(jsonNode, UpdatePasswordRequest.class);

        // 4. Business logic
        String response = userService.updatePassword(updatePasswordRequest);
        return ResponseEntity.ok(response);
    }

}
