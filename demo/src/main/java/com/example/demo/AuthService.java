package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserDto user) {
        String sanitizedUsername = InputSanitizer.sanitize(user.getUsername());
        String sanitizedEmail = InputSanitizer.sanitize(user.getEmail());
        String sanitizedPassword = InputSanitizer.sanitize(user.getPassword());

        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new RuntimeException("Email already registered");
        }

        String hashedPassword = encoder.encode(user.getPassword());

        UserEntity entity = new UserEntity(
                sanitizedUsername,
                hashedPassword,
                sanitizedEmail,
                "USER"
        );

        userRepository.save(entity);


        // Save user with hashed password (e.g., print or save to DB for now)
        System.out.println("Username: " + sanitizedUsername);
        System.out.println("Email: " + user.getEmail());
        System.out.println("Hashed Password: " + sanitizedEmail);
    }
}




