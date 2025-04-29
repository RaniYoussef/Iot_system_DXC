package com.DXC.iotbackend.controller;


import com.DXC.iotbackend.UserDto;
import com.DXC.iotbackend.model.UserEntity;
import com.DXC.iotbackend.payload.LoginRequest;
import com.DXC.iotbackend.payload.UpdatePasswordRequest;
import com.DXC.iotbackend.payload.UserProfileResponse;
import com.DXC.iotbackend.repository.UserRepository;
import com.DXC.iotbackend.service.AuthService;
import com.DXC.iotbackend.util.InputSanitizer;
import com.DXC.iotbackend.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.time.Duration;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

//import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean validateJsonFields(JsonNode jsonNode, Set<String> allowedFields) {
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            if (!allowedFields.contains(field)) return false;
        }
        for (String required : allowedFields) {
            if (!jsonNode.has(required) || jsonNode.get(required).asText().isBlank()) return false;
        }
        return true;
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto) {
//        authService.registerUser(userDto);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(Map.of("message", "User registered successfully"));
//    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto) {
        return authService.registerUser(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest,
                                   HttpServletResponse response) {
        String email = InputSanitizer.sanitize(loginRequest.getEmail());
        String password = InputSanitizer.sanitize(loginRequest.getPassword());


        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        UserEntity user = userOpt.get();



//        UserEntity user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Invalid credentials"));
//        }

        // 1. Generate JWT Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // 2. Create Secure Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true) // prevent JavaScript access
                .secure(true)   // send cookie only on HTTPS (good practice)
                .path("/")      // cookie accessible across all endpoints
                .sameSite("Strict") // CSRF protection
                .maxAge(Duration.ofHours(1)) // 1 hour expiration
                .build();

        // 3. Add cookie to response header
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 4. Return a simple success message
        return ResponseEntity.ok(Map.of("message", "Logged in successfully!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserProfile(Authentication auth) {
//        userRepository.findByUsername(auth.getName());
//        return ResponseEntity.ok("Welcome, " + auth.getName());

        return userRepository.findByUsername(auth.getName())
                .map(user -> {
                    UserProfileResponse profile = new UserProfileResponse(
                            user.getUsername(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail()
                    );
                    return ResponseEntity.ok(profile);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

    }

//    @PutMapping("/user/password")
//    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordRequest updateRequest) {
//        updateRequest.setUsername(InputSanitizer.sanitize(updateRequest.getUsername()));
//        updateRequest.setNewPassword(InputSanitizer.sanitize(updateRequest.getNewPassword()));
//
//        String response = authService.updatePassword(updateRequest);
//        return ResponseEntity.ok(response);
//    }

    @PutMapping("/user/password")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                                 Authentication authentication) {
        String result = authService.updatePassword(request, authentication);

        if (result.equals("Password updated successfully.")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Old password is incorrect.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } else if (result.equals ("New password must be different from the old password." ))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }



}
