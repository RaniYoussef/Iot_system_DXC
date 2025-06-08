package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.UserDto;
import com.dxc.iotbackend.model.UserEntity;
import com.dxc.iotbackend.payload.LoginRequest;
import com.dxc.iotbackend.payload.UpdatePasswordRequest;
import com.dxc.iotbackend.payload.UserProfileResponse;
import com.dxc.iotbackend.repository.UserRepository;
import com.dxc.iotbackend.service.AuthService;
import com.dxc.iotbackend.service.EmailService;
import com.dxc.iotbackend.util.InputSanitizer;
import com.dxc.iotbackend.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("${api.base-path}")
public class AppController {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EmailService emailService;

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

    @PostMapping("${auth.register}")
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto) {
        return authService.registerUser(userDto);
    }

    @PostMapping("${auth.login}")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest,
                                   HttpServletResponse response) {
        String email = InputSanitizer.sanitize(loginRequest.getEmail());
        String password = InputSanitizer.sanitize(loginRequest.getPassword());

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        UserEntity user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged in successfully!"));
    }

    @GetMapping("${auth.profile}")
    public ResponseEntity<?> getUserProfile(Authentication auth) {
        String identity = auth.getName();
        return userRepository.findByUsername(identity)
                .or(() -> userRepository.findByEmail(identity))
                .map(user -> ResponseEntity.ok(new UserProfileResponse(
                        user.getUsername(), user.getFirstName(), user.getLastName(),
                        user.getEmail(), user.getProfilePhoto(), user.getPhoneNumber()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("${auth.profile}${auth.verifyPassword}")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String rawPassword = body.get("password");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPassword() == null) {
            return ResponseEntity.ok(Map.of("valid", true, "passwordNull", true));
        }

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        return matches
                ? ResponseEntity.ok(Map.of("valid", true))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("valid", false, "message", "Incorrect password"));
    }

    @GetMapping("${auth.profile}${auth.passwordNull}")
    public ResponseEntity<?> isPasswordNull(Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> userOpt = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username));
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));

        boolean isNull = userOpt.get().getPassword() == null;
        return ResponseEntity.ok(Map.of("passwordNull", isNull));
    }

    @PutMapping("${auth.profile}${auth.updatePassword}")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                                              Authentication authentication) {
        String result = authService.updatePassword(request, authentication);
        return switch (result) {
            case "Password updated successfully." ->
                    ResponseEntity.ok(Map.of("success", true, "message", result));
            case "Old password is incorrect." ->
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", result));
            case "New password must be different from the old password." ->
                    ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            default ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Unexpected error."));
        };
    }

    @PostMapping("${auth.forgotPassword}")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        UserEntity user = userOpt.get();
        user.setResetToken(token);
        user.setResetTokenExpiry(expiry);
        userRepository.save(user);

        try {
            emailService.sendResetEmail(email, token);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to send email"));
        }

        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email"));
    }

    @PostMapping("${auth.resetPassword}")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> body) {
        Optional<UserEntity> userOpt = userRepository.findByResetTokenAndResetTokenExpiryAfter(token, LocalDateTime.now());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));

        String newPassword = body.get("newPassword");
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (newPassword == null || !newPassword.matches(pattern)) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Password must be at least 8 characters and include upper, lower, digit, and special character."));
        }

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @GetMapping("${auth.oauthSuccess}")
    public ResponseEntity<?> handleGoogleLogin(Authentication authentication, HttpServletResponse response) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity(
                            email,          // username
                            email,          // email
                            null,           // password
                            name,           // first name
                            "",             // last name
                            "ROLE_USER",    // role
                            null            // profile photo
                    );
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged in with Google", "user", user.getEmail()));
    }

    @GetMapping("${auth.logout}")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @PostMapping("${auth.profile}${auth.updateProfile}")
    public ResponseEntity<?> updateUserInfo(@RequestBody Map<String, String> body, Authentication auth) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()));
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));

        UserEntity user = userOpt.get();

        String newEmail = body.get("email");
        if (newEmail != null && (!newEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") ||
                (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or duplicate email"));
        }

        String newFirstName = body.get("firstName");
        if (newFirstName != null && !newFirstName.matches("^[A-Za-z\\s]{2,20}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid first name"));
        }

        String newLastName = body.get("lastName");
        if (newLastName != null && !newLastName.matches("^[A-Za-z\\s]{2,20}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid last name"));
        }

        String newPhone = body.get("phoneNumber");
        if (newPhone != null && !newPhone.matches("^[0-9]{10,15}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid phone number"));
        }

        if (newEmail != null) user.setEmail(newEmail);
        if (newFirstName != null) user.setFirstName(newFirstName);
        if (newLastName != null) user.setLastName(newLastName);
        if (newPhone != null) user.setPhoneNumber(newPhone);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User info updated successfully"));
    }

    @PutMapping("${auth.profile}${auth.updatePhoto}")
    public ResponseEntity<?> updateProfilePhoto(@RequestBody Map<String, String> body, Authentication auth) {
        String base64Photo = body.get("profilePhoto");
        if (base64Photo == null || base64Photo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Photo data is required"));
        }

        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()));
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));

        UserEntity user = userOpt.get();
        user.setProfilePhoto(base64Photo);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile photo updated"));
    }
}