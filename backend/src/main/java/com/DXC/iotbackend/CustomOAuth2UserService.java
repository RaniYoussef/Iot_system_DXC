package com.dxc.iotbackend;

//package com.dxc.iotbackend.oauth2;

import com.dxc.iotbackend.model.UserEntity;
import com.dxc.iotbackend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");  // ✅ First name
        String lastName = oAuth2User.getAttribute("family_name");  // ✅ Last name

        // Check if user exists
        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();



            // Auto-generate username
            String baseUsername = email.split("@")[0];
            String generatedUsername = baseUsername;
            int i = 1;
            while (userRepository.existsByUsername(generatedUsername)) {
                generatedUsername = baseUsername + i;
                i++;
            }

            newUser.setEmail(email);
            newUser.setFirstName(firstName); // ✅ Save first name
            newUser.setLastName(lastName);   // ✅ Save last name
            newUser.setUsername(generatedUsername);
            newUser.setRole("USER");

            return userRepository.save(newUser);
        });

        // ✅ Return a DefaultOAuth2User with authorities
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                oAuth2User.getAttributes(),
                "email"
        );
    }


//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String email = oAuth2User.getAttribute("email");
//
//        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
//            UserEntity newUser = new UserEntity();
//            newUser.setEmail(email);
//            newUser.setUsername(email.split("@")[0]);
//            newUser.setRole("ROLE_USER");
//            return userRepository.save(newUser);
//        });
//
//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
//                oAuth2User.getAttributes(),
//                "email"
//        );
//    }
}
