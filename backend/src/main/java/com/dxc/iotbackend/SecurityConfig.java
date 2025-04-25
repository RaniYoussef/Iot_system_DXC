package com.dxc.iotbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())            // disable CSRF for testing APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup", "/api/signin","/api/profile","api/profile/password").permitAll()
                        .anyRequest().authenticated()          // everything else requires auth
                )
                .httpBasic(Customizer.withDefaults());   // or .formLogin() if you like

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
