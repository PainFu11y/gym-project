package com.gym_project.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorTest {

    @Test
    void shouldReturnBaseUsernameWhenNoExistingUsernames() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of()
        );
        assertEquals("john.doe", result);
    }

    @Test
    void shouldAppend1WhenBaseUsernameAlreadyExists() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of("john.doe")
        );
        assertEquals("john.doe1", result);
    }

    @Test
    void shouldIncrementIndexWhenUsernameWithIndexExists() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of("john.doe1")
        );
        assertEquals("john.doe2", result);
    }

    @Test
    void shouldFindMaxIndexAndIncrementIt() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of("john.doe", "john.doe1", "john.doe5")
        );
        assertEquals("john.doe6", result);
    }

    @Test
    void shouldIgnoreInvalidSuffixes() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of("john.doeX", "john.doe2")
        );
        assertEquals("john.doe3", result);
    }

    @Test
    void shouldIgnoreOtherUsernames() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of("johnnes.doering","alice.smith", "bob.jones", "johnsson.doelman")
        );
        assertEquals("john.doe", result);
    }

    @Test
    void shouldHandleLargeIndexes() {
        String result = UsernameGenerator.generate(
                "john",
                "doe",
                List.of("john.doe9999999")
        );
        assertEquals("john.doe10000000", result);
    }
}