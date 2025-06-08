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

    private static final String MESSAGE = "message";
    private static final String SUCCESS = "success";
    private static final String VALID = "valid";
    private static final String USER_NOT_FOUND = "User not found";

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EmailService emailService;

    @PostMapping("${auth.register}")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid UserDto userDto) {
        return authService.registerUser(userDto);
    }

    @PostMapping("${auth.login}")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        String email = InputSanitizer.sanitize(loginRequest.getEmail());
        String password = InputSanitizer.sanitize(loginRequest.getPassword());

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        UserEntity user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true).secure(false).path("/").sameSite("Lax")
                .maxAge(Duration.ofHours(1)).build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of(MESSAGE, "Logged in successfully!"));
    }

    @GetMapping("${auth.profile}")
    public ResponseEntity<?> getUserProfile(Authentication auth) {
        String identity = auth.getName();
        return userRepository.findByUsername(identity)
                .or(() -> userRepository.findByEmail(identity))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(
                        new UserProfileResponse(
                                user.getUsername(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getProfilePhoto(),
                                user.getPhoneNumber()
                        )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND)));
    }

    @PostMapping("${auth.profile}${auth.verifyPassword}")
    public ResponseEntity<Map<String, Object>> verifyPassword(@RequestBody Map<String, String> body) {
        String rawPassword = body.get("password");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        if (user.getPassword() == null) {
            return ResponseEntity.ok(Map.of(VALID, true, "passwordNull", true));
        }

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if (matches) {
            return ResponseEntity.ok(Map.of(VALID, true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(VALID, false, MESSAGE, "Incorrect password"));
        }
    }

    @GetMapping("${auth.profile}${auth.passwordNull}")
    public ResponseEntity<Map<String, Boolean>> isPasswordNull(Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> userOpt = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username));

        return userOpt.map(user -> ResponseEntity.ok(Map.of("passwordNull", user.getPassword() == null)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of()));
    }

    @PutMapping("${auth.profile}${auth.updatePassword}")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                                              Authentication authentication) {
        String result = authService.updatePassword(request, authentication);
        return switch (result) {
            case "Password updated successfully." -> ResponseEntity.ok(Map.of(SUCCESS, true, MESSAGE, result));
            case "Old password is incorrect.", "New password must be different from the old password." ->
                    ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, result));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(SUCCESS, false, MESSAGE, "Unexpected error."));
        };
    }

    @PostMapping("${auth.forgotPassword}")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
        }

        UserEntity user = userOpt.get();
        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        try {
            emailService.sendResetEmail(email, user.getResetToken());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(MESSAGE, "Failed to send email"));
        }

        return ResponseEntity.ok(Map.of(MESSAGE, "Reset link sent to your email"));
    }

    @PostMapping("${auth.resetPassword}")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String token,
                                                             @RequestBody Map<String, String> body) {
        Optional<UserEntity> userOpt = userRepository.findByResetTokenAndResetTokenExpiryAfter(token, LocalDateTime.now());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Invalid or expired token"));
        }

        String newPassword = body.get("newPassword");
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (newPassword == null || !newPassword.matches(pattern)) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Password must be at least 8 characters and include upper, lower, digit, and special character."));
        }

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(MESSAGE, "Password updated successfully"));
    }

    @GetMapping("${auth.oauthSuccess}")
    public ResponseEntity<Map<String, Object>> handleGoogleLogin(Authentication authentication, HttpServletResponse response) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new UserEntity(email, email, null, name, "", "ROLE_USER", null)));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(Duration.ofHours(1)).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of(MESSAGE, "Logged in with Google", "user", user.getEmail()));
    }

    @GetMapping("${auth.logout}")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(0).build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of(MESSAGE, "Logged out"));
    }

    @PostMapping("${auth.profile}${auth.updateProfile}")
    public ResponseEntity<Map<String, String>> updateUserInfo(@RequestBody Map<String, String> body, Authentication auth) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
        }

        UserEntity user = userOpt.get();

        String newEmail = body.get("email");
        if (newEmail != null && (!newEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") ||
                (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)))) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Invalid or duplicate email"));
        }

        String newFirstName = body.get("firstName");
        if (newFirstName != null && !newFirstName.matches("^[A-Za-z\\s]{2,20}$")) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Invalid first name"));
        }

        String newLastName = body.get("lastName");
        if (newLastName != null && !newLastName.matches("^[A-Za-z\\s]{2,20}$")) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Invalid last name"));
        }

        String newPhone = body.get("phoneNumber");
        if (newPhone != null && !newPhone.matches("^\\d{10,15}$")) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Invalid phone number"));
        }

        if (newEmail != null) user.setEmail(newEmail);
        if (newFirstName != null) user.setFirstName(newFirstName);
        if (newLastName != null) user.setLastName(newLastName);
        if (newPhone != null) user.setPhoneNumber(newPhone);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of(MESSAGE, "User info updated successfully"));
    }

    @PutMapping("${auth.profile}${auth.updatePhoto}")
    public ResponseEntity<Map<String, String>> updateProfilePhoto(@RequestBody Map<String, String> body, Authentication auth) {
        String base64Photo = body.get("profilePhoto");
        if (base64Photo == null || base64Photo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, "Photo data is required"));
        }

        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
        }

        UserEntity user = userOpt.get();
        user.setProfilePhoto(base64Photo);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of(MESSAGE, "Profile photo updated"));
    }
}
