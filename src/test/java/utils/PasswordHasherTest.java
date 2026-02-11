package utils;

import gym.utils.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new PasswordHasher();
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                passwordHasher.hash(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                passwordHasher.hash(new char[0])
        );
    }

    @Test
    void shouldReturnSameHashForSamePassword() {
        char[] password = "secret123".toCharArray();

        char[] hash1 = passwordHasher.hash(password);
        char[] hash2 = passwordHasher.hash(password);

        assertArrayEquals(hash1, hash2);
    }

    @Test
    void shouldReturnDifferentHashForDifferentPasswords() {
        char[] hash1 = passwordHasher.hash("password1".toCharArray());
        char[] hash2 = passwordHasher.hash("password2".toCharArray());

        assertFalse(java.util.Arrays.equals(hash1, hash2));
    }

    @Test
    void shouldReturnSha256HexHash() {
        char[] password = "password".toCharArray();

        char[] hash = passwordHasher.hash(password);

        assertEquals(64, hash.length);
    }

    @Test
    void shouldGenerateCorrectSha256Hash() {
        char[] password = "password".toCharArray();

        char[] hash = passwordHasher.hash(password);

        String expected =
                "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";

        assertEquals(expected, new String(hash));
    }
}
