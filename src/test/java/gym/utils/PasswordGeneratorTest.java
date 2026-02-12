package gym.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String password = generator.generate();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void shouldGeneratePasswordWithOnlyAllowedCharacters() {
        String password = generator.generate();

        String allowedChars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (char c : password.toCharArray()) {
            assertTrue(allowedChars.indexOf(c) >= 0,
                    "Invalid character found: " + c);
        }
    }

    @Test
    void shouldGenerateDifferentPasswords() {
        String password1 = generator.generate();
        String password2 = generator.generate();

        assertNotEquals(password1, password2);
    }

    @Test
    void shouldGenerateMultiplePasswordsWithoutErrors() {
        for (int i = 0; i < 1000; i++) {
            String password = generator.generate();
            assertEquals(10, password.length());
        }
    }
}