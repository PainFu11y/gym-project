package com.gym_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceJwtServiceTest {

    private static final String SECRET          = "s3rv1c3S3cr3tK3yF0rM1cr0s3rv1c3C0mm";
    private static final long   EXPIRATION_MS   = 86400000L; // 24h

    private ServiceJwtService serviceJwtService;
    private SecretKey          secretKey;

    @BeforeEach
    void setUp() {
        serviceJwtService = new ServiceJwtService(SECRET, EXPIRATION_MS);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Test
    void generateServiceToken_shouldReturnNonNullNonBlankToken() {
        String token = serviceJwtService.generateServiceToken();
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateServiceToken_shouldHaveTypeServiceClaim() {
        String token = serviceJwtService.generateServiceToken();
        Claims claims = parseToken(token);
        assertThat(claims.get("type", String.class)).isEqualTo("SERVICE");
    }

    @Test
    void generateServiceToken_shouldHaveSubjectGymService() {
        String token = serviceJwtService.generateServiceToken();
        Claims claims = parseToken(token);
        assertThat(claims.getSubject()).isEqualTo("gym-service");
    }

    @Test
    void generateServiceToken_shouldNotBeExpired() {
        String token = serviceJwtService.generateServiceToken();
        Claims claims = parseToken(token);
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void generateServiceToken_shouldExpireAfterConfiguredDuration() {
        long before = System.currentTimeMillis();
        String token = serviceJwtService.generateServiceToken();
        long after  = System.currentTimeMillis();

        Claims claims = parseToken(token);
        long expirationTime = claims.getExpiration().getTime();

        assertThat(expirationTime).isBetween(before + EXPIRATION_MS - 1000, after + EXPIRATION_MS + 1000);
    }

    @Test
    void generateServiceToken_shouldHaveIssuedAtSetToNow() {
        long before = System.currentTimeMillis();
        String token = serviceJwtService.generateServiceToken();
        long after  = System.currentTimeMillis();

        Claims claims = parseToken(token);
        long issuedAt = claims.getIssuedAt().getTime();

        assertThat(issuedAt).isBetween(before - 1000, after + 1000);
    }

    @Test
    void generateServiceToken_shouldBeVerifiableWithCorrectSecret() {
        String token = serviceJwtService.generateServiceToken();

        Claims claims = parseToken(token);
        assertThat(claims).isNotNull();
    }
}
