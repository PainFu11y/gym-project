package com.gym_report.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceJwtValidatorTest {

    private static final String SECRET  = "s3rv1c3S3cr3tK3yF0rM1cr0s3rv1c3C0mm";
    private static final String WRONG_SECRET = "wr0ngS3cr3tK3yTh4tIsD1ff3r3ntXXXXXX";

    private ServiceJwtValidator validator;
    private SecretKey            secretKey;

    @BeforeEach
    void setUp() {
        validator = new ServiceJwtValidator(SECRET);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String buildToken(String type, long expiresInMs) {
        return Jwts.builder()
                .subject("gym-service")
                .claims(Map.of("type", type))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiresInMs))
                .signWith(secretKey)
                .compact();
    }


    @Test
    void isValidServiceToken_shouldReturnTrue_whenTokenIsValid() {
        String token = buildToken("SERVICE", 86400000L);
        assertThat(validator.isValidServiceToken(token)).isTrue();
    }

    @Test
    void isValidServiceToken_shouldReturnTrue_forFreshToken() {
        String token = buildToken("SERVICE", 60000L); // 1 min
        assertThat(validator.isValidServiceToken(token)).isTrue();
    }

    @Test
    void isValidServiceToken_shouldReturnFalse_whenTypeIsUser() {
        String token = buildToken("USER", 86400000L);
        assertThat(validator.isValidServiceToken(token)).isFalse();
    }

    @Test
    void isValidServiceToken_shouldReturnFalse_whenTypeClaimIsMissing() {
        String token = Jwts.builder()
                .subject("gym-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(secretKey)
                .compact();
        assertThat(validator.isValidServiceToken(token)).isFalse();
    }

    @Test
    void isValidServiceToken_shouldReturnFalse_whenTypeIsEmptyString() {
        String token = buildToken("", 86400000L);
        assertThat(validator.isValidServiceToken(token)).isFalse();
    }


    @Test
    void isValidServiceToken_shouldReturnFalse_whenTokenIsExpired() {
        String token = Jwts.builder()
                .subject("gym-service")
                .claims(Map.of("type", "SERVICE"))
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000)) // already expired
                .signWith(secretKey)
                .compact();

        assertThat(validator.isValidServiceToken(token)).isFalse();
    }


    @Test
    void isValidServiceToken_shouldReturnFalse_whenSignedWithWrongKey() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(WRONG_SECRET.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .subject("gym-service")
                .claims(Map.of("type", "SERVICE"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(wrongKey)
                .compact();

        assertThat(validator.isValidServiceToken(token)).isFalse();
    }


    @Test
    void isValidServiceToken_shouldReturnFalse_whenTokenIsGarbage() {
        assertThat(validator.isValidServiceToken("not.a.jwt")).isFalse();
    }

    @Test
    void isValidServiceToken_shouldReturnFalse_whenTokenIsEmpty() {
        assertThat(validator.isValidServiceToken("")).isFalse();
    }

    @Test
    void isValidServiceToken_shouldReturnFalse_whenTokenIsBlank() {
        assertThat(validator.isValidServiceToken("   ")).isFalse();
    }
}
