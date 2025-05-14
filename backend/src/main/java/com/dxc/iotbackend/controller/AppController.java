package com.DXC.iotbackend.controller;


import com.DXC.iotbackend.UserDto;
import com.DXC.iotbackend.model.UserEntity;
import com.DXC.iotbackend.payload.LoginRequest;
import com.DXC.iotbackend.payload.UpdatePasswordRequest;
import com.DXC.iotbackend.payload.UserProfileResponse;
import com.DXC.iotbackend.repository.UserRepository;
import com.DXC.iotbackend.service.AuthService;
import com.DXC.iotbackend.service.EmailService;
import com.DXC.iotbackend.util.InputSanitizer;
import com.DXC.iotbackend.util.JwtUtil;
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

import java.time.LocalDateTime;
import java.util.*;

import java.time.Duration;

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
                .secure(false)   // send cookie only on HTTPS (good practice) -- but since on local host false
                .path("/")      // cookie accessible across all endpoints
                //.sameSite("Strict") // CSRF protection
                .sameSite("Lax") //protect against CSRF in Put,Delete and Post -- not as strong as Strict -- but needs to be used since we use different ports for backend and frontend -- None since it doesn't work using either strict or lax
                .maxAge(Duration.ofHours(1)) // 1 hour expiration
                .build();

        // 3. Add cookie to response header
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 4. Return a simple success message
        return ResponseEntity.ok(Map.of("message", "Logged in successfully!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserProfile(Authentication auth) {
        String identity = auth.getName();

        return userRepository.findByUsername(identity)
                .or(() -> userRepository.findByEmail(identity)) // 👈 fallback
                .map(user -> {
                    UserProfileResponse profile = new UserProfileResponse(
                            user.getUsername(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getProfilePhoto(),
                            user.getPhoneNumber()
                            //user.isOAuthUser()
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


    @PostMapping("user/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String rawPassword = body.get("password");

        // Extract username from JWT (already authenticated)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPassword() == null) {
            return ResponseEntity.ok(Map.of("valid", true, "passwordNull", true)); // ✅ Skip verification
        }

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return ResponseEntity.ok(Map.of("valid", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "message", "Incorrect password"));
        }
    }

    @GetMapping("/user/password-null")
    public ResponseEntity<?> isPasswordNull(Authentication authentication) {
        String username = authentication.getName();

        Optional<UserEntity> userOpt = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        boolean isNull = (userOpt.get().getPassword() == null);
        return ResponseEntity.ok(Map.of("passwordNull", isNull));
    }





    @PutMapping("user/update-password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                                 Authentication authentication) {

        System.out.println("Old: " + request.getOldPassword());
        System.out.println("New: " + request.getNewPassword());
        System.out.println("Auth User: " + authentication.getName());

        String result = authService.updatePassword(request, authentication);
        if (result.equals("Password updated successfully.")) {
            return ResponseEntity.ok(Map.of("success", true, "message", result));
        } else if (result.equals("Old password is incorrect.")) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", result));
        } else if (result.equals ("New password must be different from the old password." ))
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));

        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Unexpected error."));
        }
    }

    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        UserEntity user = userOpt.get();
        user.setResetToken(token);
        user.setResetTokenExpiry(expiry);
        userRepository.save(user);

        try {
            emailService.sendResetEmail(email, token);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send email"));
        }

        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email"));
    }






//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
//        String email = body.get("email");
//        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
//
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "User not found"));
//        }
//
//        // Generate a token (can reuse JWT or random UUID)
//        String token = UUID.randomUUID().toString();
//        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
//
//        userOpt.get().setResetToken(token);
//        userOpt.get().setResetTokenExpiry(expiry);
//        userRepository.save(userOpt.get());
//
//        // send email with link: http://localhost:4200/reset-password?token=XYZ
//
//
////        // Save token to DB or cache with expiry (optional step)
////        userOpt.get().setResetToken(token);
////        userRepository.save(userOpt.get());
//
//        // Send email (simulate with console print or real service)
//        String resetLink = "http://localhost:4200/reset-password?token=" + token;
//        System.out.println("🔐 Reset link: " + resetLink);
//
//        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email"));
//    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
//        String token = body.get("token");
//        String newPassword = body.get("newPassword");
//
//        Optional<UserEntity> userOpt = userRepository.findByResetToken(token);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("message", "Invalid or expired token"));
//        }
//
//        UserEntity user = userOpt.get();
//        user.setPassword(passwordEncoder.encode(newPassword));
//        user.setResetToken(null); // Invalidate token
//        userRepository.save(user);
//
//        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
//    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> body) {
        Optional<UserEntity> userOpt = userRepository.findByResetTokenAndResetTokenExpiryAfter(token, LocalDateTime.now());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid or expired token"));
        }

        String newPassword = body.get("newPassword");

        // ✅ Strong password regex: min 8 chars, 1 upper, 1 lower, 1 digit, 1 special
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (newPassword == null || !newPassword.matches(passwordPattern)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character."));
        }

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> handleGoogleLogin(Authentication authentication, HttpServletResponse response) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Find or create user
        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setFirstName(name);
                    newUser.setRole("ROLE_USER");
                    //newUser.setOAuthUser(true);
                    return userRepository.save(newUser);
                });

        // Generate JWT
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Set JWT as HttpOnly cookie
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


    @GetMapping("/logout")
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

    @PostMapping("/user/update-profile")
    public ResponseEntity<?> updateUserInfo(@RequestBody Map<String, String> body, Authentication auth) {
        String newFirstName = body.get("firstName");
        String newLastName = body.get("lastName");
        String newEmail = body.get("email");
        String newPhoneNumber = body.get("phoneNumber");
        //String profilePhoto = body.get("profilePhoto");

        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        UserEntity user = userOpt.get();

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email already in use"));
            }
            user.setEmail(newEmail);
        }


        if (newFirstName != null) user.setFirstName(newFirstName);
        if (newLastName != null) user.setLastName(newLastName);
        if (newPhoneNumber != null) user.setPhoneNumber(newPhoneNumber);
        //if (profilePhoto != null) user.setProfilePhoto(profilePhoto);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User info updated successfully"));
    }



    @PutMapping("/user/update-photo")
    public ResponseEntity<?> updateProfilePhoto(@RequestBody Map<String, String> body, Authentication auth) {
        String base64Photo = body.get("profilePhoto");

        if (base64Photo == null || base64Photo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Photo data is required"));
        }

        System.out.println("Photo size: " + base64Photo.length());

        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
        UserEntity user = userOpt.get();
        user.setProfilePhoto(base64Photo);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile photo updated"));
    }











}
