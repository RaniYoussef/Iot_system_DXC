package com.dxc.iotbackend.service;

import com.dxc.iotbackend.UserDto;
import com.dxc.iotbackend.model.UserEntity;
import com.dxc.iotbackend.payload.UpdatePasswordRequest;
import com.dxc.iotbackend.repository.UserRepository;
import com.dxc.iotbackend.util.InputSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Map<String, String>> registerUser(UserDto user) {
        String sanitizedUsername = InputSanitizer.sanitize(user.getUsername());
        String sanitizedEmail = InputSanitizer.sanitize(user.getEmail());
        String sanitizedFirstName = InputSanitizer.sanitize(user.getFirstName());
        String sanitizedLastName = InputSanitizer.sanitize(user.getLastName());
        String sanitizedPhoneNumber = InputSanitizer.sanitize(user.getPhoneNumber());

        if (userRepository.existsByEmail(sanitizedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        if (userRepository.existsByUsername(sanitizedUsername)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already registered"));
        }

        UserEntity entity = new UserEntity(
                sanitizedUsername,
                encoder.encode(user.getPassword()),
                sanitizedEmail,
                "USER",
                sanitizedFirstName,
                sanitizedLastName,
                sanitizedPhoneNumber
        );

        userRepository.save(entity);
        logger.info("User registered: username={}, email={}", sanitizedUsername, sanitizedEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    public String updatePassword(UpdatePasswordRequest request, Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return "User not found.";
        }

        UserEntity user = userOptional.get();
        String currentPassword = user.getPassword();

        if (currentPassword == null || currentPassword.isBlank()) {
            user.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return "Password created successfully.";
        }

        if (!encoder.matches(request.getOldPassword(), currentPassword)) {
            return "Old password is incorrect.";
        }

        if (encoder.matches(request.getNewPassword(), currentPassword)) {
            return "New password must be different from the old password.";
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password updated successfully.";
    }
}
