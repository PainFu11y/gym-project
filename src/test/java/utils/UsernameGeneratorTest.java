package utils;

import gym.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorTest {

    private UsernameGenerator usernameGenerator;

    @BeforeEach
    void setUp() {
        usernameGenerator = new UsernameGenerator();
    }

    @Test
    void shouldReturnBaseUsernameWhenNoExistingUsernames() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of()
        );

        assertEquals("john.doe", result);
    }

    @Test
    void shouldAppend1WhenBaseUsernameAlreadyExists() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of("john.doe")
        );

        assertEquals("john.doe1", result);
    }

    @Test
    void shouldIncrementIndexWhenUsernameWithIndexExists() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of("john.doe1")
        );

        assertEquals("john.doe2", result);
    }

    @Test
    void shouldFindMaxIndexAndIncrementIt() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of("john.doe", "john.doe1", "john.doe5")
        );

        assertEquals("john.doe6", result);
    }

    @Test
    void shouldIgnoreInvalidSuffixes() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of("john.doeX", "john.doe2")
        );

        assertEquals("john.doe3", result);
    }

    @Test
    void shouldIgnoreOtherUsernames() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of("johnnes.doering","alice.smith", "bob.jones", "johnsson.doelman")
        );

        assertEquals("john.doe", result);
    }

    @Test
    void shouldHandleLargeIndexes() {
        String result = usernameGenerator.generate(
                "john",
                "doe",
                Set.of("john.doe9999999")
        );

        assertEquals("john.doe10000000", result);
    }

}
