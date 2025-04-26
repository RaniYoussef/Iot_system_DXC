package com.DXC.iotbackend.service;

import com.DXC.iotbackend.UserDto;
import com.DXC.iotbackend.model.UserEntity;
import com.DXC.iotbackend.util.InputSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.DXC.iotbackend.repository.UserRepository;


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
        String sanitizedFirstName = InputSanitizer.sanitize(user.getFirstName());
        String sanitizedLastName = InputSanitizer.sanitize(user.getLastName());

        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new RuntimeException("Email already registered");
        }

        String hashedPassword = encoder.encode(user.getPassword());

        UserEntity entity = new UserEntity(
                sanitizedUsername,
                hashedPassword,
                sanitizedEmail,
                "USER",
                sanitizedFirstName,
                sanitizedLastName
        );

        userRepository.save(entity);


        // Save user with hashed password (e.g., print or save to DB for now)
        System.out.println("Username: " + sanitizedUsername);
        System.out.println("Email: " + user.getEmail());
        System.out.println("Hashed Password: " + sanitizedEmail);
    }
}




