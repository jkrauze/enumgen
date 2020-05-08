package io.github.jkrauze.enumgen.keynaming;

import io.github.jkrauze.enumgen.keynaming.impl.DefaultEnumKeyNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultEnumKeyNamingStrategyTest {

    Vars vars;
    Results results;

    EnumKeyNamingStrategy enumKeyNamingStrategy;

    @BeforeEach
    void setUp() {
        vars = new Vars();
        results = new Results();

        enumKeyNamingStrategy = new DefaultEnumKeyNamingStrategy();
    }

    @Test
    void number() {
        assertKeyConvertedTo("123", "_123");
    }

    @Test
    void dotStartingWord() {
        assertKeyConvertedTo(".hello", "_HELLO");
    }

    @Test
    void underscoreStartingWord() {
        assertKeyConvertedTo("_hello", "_HELLO");
    }

    @Test
    void oneWord() {
        assertKeyConvertedTo("hello", "HELLO");
    }

    @Test
    void twoWordDotJoined() {
        assertKeyConvertedTo("hello.world", "HELLO_WORLD");
    }

    @Test
    void twoWordUnderscoreJoined() {
        assertKeyConvertedTo("hello_world", "HELLO_WORLD");
    }

    @Test
    void twoWordDotUnderscoreJoined() {
        assertKeyConvertedTo("hello._world", "HELLO__WORLD");
    }

    @Test
    void twoWordUnderscoreDotJoined() {
        assertKeyConvertedTo("hello_.world", "HELLO__WORLD");
    }

    private void assertKeyConvertedTo(String key, String expectedEnumKey) {
        givenFollowingKey(key);
        whenGettingEnumKey();
        thenFollowingEnumKeyReturned(expectedEnumKey);
    }

    private void givenFollowingKey(String key) {
        vars.key = key;
    }

    private void whenGettingEnumKey() {
        results.enumKey = enumKeyNamingStrategy.getEnumKey(vars.key);
    }

    private void thenFollowingEnumKeyReturned(String expectedResult) {
        assertEquals(expectedResult, results.enumKey);
    }

    static class Vars {
        String key;
    }

    static class Results {
        String enumKey;
    }
}