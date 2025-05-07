package com.DXC.iotbackend;


import com.DXC.iotbackend.JwtFilter;
import com.DXC.iotbackend.RateLimitingFilter;
import com.DXC.iotbackend.UserDetailsImpl;
import com.DXC.iotbackend.model.UserEntity;
import com.DXC.iotbackend.repository.UserRepository;
import com.DXC.iotbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//import com.DXC.iotbackend.oauth2.CustomOAuth2UserService;
import com.DXC.iotbackend.CustomOAuth2UserService;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.time.Duration;
import java.util.List;

@Configuration
@EnableMethodSecurity

public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final RateLimitingFilter rateLimitingFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, RateLimitingFilter rateLimitingFilter) {
        this.jwtFilter = jwtFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }


    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**", "/oauth2/**", "/login/**","/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");

                            UserEntity user = userRepository.findByEmail(email).orElseThrow();

                            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

                            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .sameSite("Lax")
                                    .maxAge(Duration.ofHours(1))
                                    .build();

                            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                            response.sendRedirect("http://localhost:4200"); // adjust your frontend route
                        })

                );

        http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.headers(headers ->
//                headers.xssProtection(
//                        xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
//                ).contentSecurityPolicy(
//                        cps -> cps.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; object-src 'none'; frame-ancestors 'none';")
//                ));
//        System.out.println(" SecurityFilterChain setup completed");
//        return http
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> {})
//                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers(
//                                        "/api/login",
//                                        "/api/register",
//                                        "/api/forgot-password",
//                                        "/api/reset-password",
//                                        "/oauth2/**",
//                                        "/login/**"
//                                ).permitAll()
//                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
//                                .anyRequest().denyAll() // default deny
//                        //.anyRequest().authenticated()
//                )
//                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class) // Rate limiting first
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //JWT authentication
//                .build();
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
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie"); // <- Required
            }
        };
    }


//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:4200")); // exact origin
//        config.setAllowCredentials(true); // allow cookies
//        config.addAllowedHeader("*"); // allow all headers including Authorization
//        config.addAllowedMethod("*"); // GET, POST, etc.
//        config.setAllowCredentials(true); // 👈 MUST be true to allow cookies
//        config.setExposedHeaders(List.of("Set-Cookie")); // Optional, for visibility
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

}
