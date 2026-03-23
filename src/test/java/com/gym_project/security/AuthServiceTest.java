package com.gym_project.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_shouldSetAuthenticationInSecurityContext() {
        authService.authenticate("john", Role.TRAINEE);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("john", authentication.getName());
        assertTrue(authentication.isAuthenticated());
    }

    @Test
    void authenticate_shouldSetCorrectRole() {
        authService.authenticate("john", Role.TRAINER);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals(Role.TRAINER.asAuthority()))
        );
    }

    @Test
    void logout_shouldClearSecurityContext() {
        authService.authenticate("john", Role.TRAINEE);

        authService.logout();

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNull(authentication);
    }
}