package com.gym_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private final SecretKey key;
    private final long      expirationMs;

    public JwtService(
            @Value("${jwt.secret}")     String secret,
            @Value("${jwt.expiration}") long   expirationMs
    ) {
        this.key          = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }


    public String generateToken(UserDetails userDetails) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        String role = (userDetails instanceof GymUserDetails gym)
                ? gym.getRole().asAuthority()
                : "";

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String subject = extractUsername(token);
            return subject.equals(userDetails.getUsername()) && !isExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }


    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    private boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}