package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity

public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final RateLimitingFilter rateLimitingFilter;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println(" SecurityFilterChain setup completed");
        return http
                .csrf().disable()
                .cors().and() // enable CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //JWT authentication
                .build();


    }


//    @Autowired
//    private RateLimitingFilter rateLimitingFilter;
//
//    @Autowired
//    private JwtFilter jwtFilter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        System.out.println("✅ SecurityFilterChain setup completed");
//        return http
//                .csrf().disable()
//                .cors().and() // ✅ enable CORS
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/register").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(rateLimitingFilter, JwtFilter.class) // Rate limiting first
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //JWT authentication
//                .build();
//
//
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> null; // disables default in-memory auth
//    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new UserDetailsImpl(user.getUsername(), user.getRole());
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*"); // ✅ allow all headers including Authorization
        config.addAllowedMethod("*"); // GET, POST, etc.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }






//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .headers().frameOptions().disable()  // 👉 important for H2 Console
//                .and()
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/h2-console/**").permitAll()
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }
}

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                );
//        return http.build();
//    }