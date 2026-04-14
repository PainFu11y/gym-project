package com.gym_project.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class ServiceJwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public ServiceJwtService(
            @Value("${jwt.service.secret}") String secret,
            @Value("${jwt.service.expiration}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateServiceToken() {
        return Jwts.builder()
                .subject("gym-service")
                .claims(Map.of("type", "SERVICE"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }
}
