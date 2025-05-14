package com.DXC.iotbackend.service;

import com.DXC.iotbackend.UserDto;
import com.DXC.iotbackend.model.UserEntity;
import com.DXC.iotbackend.payload.UpdatePasswordRequest;
import com.DXC.iotbackend.util.InputSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.DXC.iotbackend.repository.UserRepository;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;


@Service
public class AuthService {
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> registerUser(UserDto user) {
        String sanitizedUsername = InputSanitizer.sanitize(user.getUsername());
        String sanitizedEmail = InputSanitizer.sanitize(user.getEmail());
        String sanitizedPassword = InputSanitizer.sanitize(user.getPassword());
        String sanitizedFirstName = InputSanitizer.sanitize(user.getFirstName());
        String sanitizedLastName = InputSanitizer.sanitize(user.getLastName());
        String sanitizedPhoneNumber = InputSanitizer.sanitize(user.getPhoneNumber());



        if (userRepository.existsByEmail(sanitizedEmail)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        if (userRepository.existsByUsername(sanitizedUsername)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already registered"));
        }


//        if (userRepository.existsByEmail(sanitizedEmail)) {
//            throw new RuntimeException("Email already registered");
//        }
//        if (userRepository.existsByUsername(sanitizedUsername)) {
//            throw new RuntimeException("Username already registered");
//        }

        String hashedPassword = encoder.encode(user.getPassword());

        UserEntity entity = new UserEntity(
                sanitizedUsername,
                hashedPassword,
                sanitizedEmail,
                "USER",
                sanitizedFirstName,
                sanitizedLastName,
                sanitizedPhoneNumber
        );

        //entity.setTokenIssuedAt(System.currentTimeMillis());
        userRepository.save(entity);


        // Save user with hashed password (e.g., print or save to DB for now)
        System.out.println("Username: " + sanitizedUsername);
        System.out.println("Email: " + user.getEmail());
        System.out.println("Hashed Password: " + sanitizedEmail);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));

    }

//    public User getUserProfile(String email) {
//        return userRepository.findByEmail(email).orElse(null);
//    }


    public String updatePassword(UpdatePasswordRequest request, Authentication authentication) {
        String username = authentication.getName(); // Username extracted from JWT!

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            String currentPassword = user.getPassword(); // Can be null

            if (currentPassword == null || currentPassword.isBlank()) {
                String hashedPassword = encoder.encode(request.getNewPassword());
                user.setPassword(hashedPassword);
                userRepository.save(user);
                return "Password created successfully.";
            }
            // Verify old password for double security
            if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
                return "Old password is incorrect.";
            }
            if (encoder.matches(request.getNewPassword(), user.getPassword())) {
                return "New password must be different from the old password.";
            }

            String hashedPassword = encoder.encode(request.getNewPassword());
            user.setPassword(hashedPassword);

            userRepository.save(user);
            return "Password updated successfully.";
        } else {
            return "User not found.";
        }
    }



}




