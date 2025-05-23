//package com.dxc.iotbackend.service;
//
//import com.dxc.iotbackend.model.User;
//import com.dxc.iotbackend.payload.SignInRequest;
//import com.dxc.iotbackend.payload.SignUpRequest;
//import com.dxc.iotbackend.payload.UpdatePasswordRequest;
//import com.dxc.iotbackend.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//
//    public String registerUser(SignUpRequest request) {
//        System.out.println("🛎️ registerUser() invoked with: " + request);
//        if (userRepository.existsByEmail(request.getEmail())) {
//            return "Email is already in use.";
//        }
//
//        User user = new User();
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//
//        System.out.println("Saving user with email: " + user.getEmail());
//        user.setEmail(request.getEmail());
//
//        String hashedPassword = passwordEncoder.encode(request.getPassword());
//        user.setPassword(hashedPassword);
//
//        //user.setPassword(request.getPassword());
//
//        userRepository.save(user);
//        return "User registered successfully.";
//    }
//
//    public String authenticateUser(SignInRequest request) {
//        Optional<User> user = userRepository.findByEmail(request.getEmail());
//
//        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
//            return "Signed in successfully!";
//        } else {
//            return "Invalid credentials.";
//        }
//    }
//
//    public User getUserProfile(String email) {
//        return userRepository.findByEmail(email).orElse(null);
//    }
//
//    public String updatePassword(UpdatePasswordRequest request) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            //user.setPassword(request.getNewPassword()); // You should hash this in production
//            String hashedPassword = passwordEncoder.encode(request.getNewPassword());
//            user.setPassword(hashedPassword);
//
//            userRepository.save(user);
//            return "Password updated successfully.";
//        } else {
//            return "User not found.";
//        }
//    }
//}
//
