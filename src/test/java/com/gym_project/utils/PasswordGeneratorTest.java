package com.gym_project.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String password = PasswordGenerator.generate();

        assertNotNull(password, "Password should not be null");
        assertEquals(10, password.length(), "Password length should be 10");
    }

    @Test
    void shouldGeneratePasswordWithOnlyAllowedCharacters() {
        String password = PasswordGenerator.generate();

        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (char c : password.toCharArray()) {
            assertTrue(allowedChars.indexOf(c) >= 0,
                    "Invalid character found: " + c);
        }
    }

    @Test
    void shouldGenerateDifferentPasswords() {
        String password1 = PasswordGenerator.generate();
        String password2 = PasswordGenerator.generate();

        assertNotEquals(password1, password2, "Passwords should not be equal");
    }

    @Test
    void shouldGenerateMultiplePasswordsWithoutErrors() {
        for (int i = 0; i < 1000; i++) {
            String password = PasswordGenerator.generate();
            assertEquals(10, password.length(), "Password length should always be 10");
        }
    }
}