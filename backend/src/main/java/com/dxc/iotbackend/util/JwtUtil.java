package com.dxc.iotbackend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRATION_MILLIS = 1000L * 60 * 60; // 1 hour
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate a JWT token containing username and role.
     *
     * @param username the user identifier
     * @param role     the user role
     * @return signed JWT token
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MILLIS);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the username (subject) from a JWT token.
     */
    public String extractUsername(String token) {
        return parseToken(token).getBody().getSubject();
    }

    /**
     * Extract the user role from a JWT token.
     */
    public String extractRole(String token) {
        return parseToken(token).getBody().get("role", String.class);
    }

    /**
     * Extract the token's issued date.
     */
    public Date extractIssuedAt(String token) {
        return parseToken(token).getBody().getIssuedAt();
    }

    /**
     * Validate the structure and signature of a JWT token.
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Common method to parse token with key and return claims.
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
