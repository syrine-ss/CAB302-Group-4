package com.cab302.vic.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour-focused tests for {@link PasswordHasher}.
 * Written before the implementation was hardened (Red then Green then Refactor):
 * the first pass had a bug in null handling, tests caught it, fix made them pass.
 */
class PasswordHasherTest {

    @Test
    void hashProducesSaltHashFormat() {
        String stored = PasswordHasher.hash("Correct!Horse1");
        assertTrue(stored.contains(":"),
                "Stored value should be salt and hash separated by ':'");
        String[] parts = stored.split(":");
        assertEquals(2, parts.length);
        assertFalse(parts[0].isEmpty(), "Salt part must not be empty");
        assertFalse(parts[1].isEmpty(), "Hash part must not be empty");
    }

    @Test
    void hashingSamePasswordTwiceProducesDifferentResults() {
        // Each hash uses a new random salt, so identical passwords should
        // NOT collide, otherwise the salt isn't doing its job.
        String a = PasswordHasher.hash("Correct!Horse1");
        String b = PasswordHasher.hash("Correct!Horse1");
        assertNotEquals(a, b);
    }

    @Test
    void verifyReturnsTrueForCorrectPassword() {
        String stored = PasswordHasher.hash("Correct!Horse1");
        assertTrue(PasswordHasher.verify("Correct!Horse1", stored));
    }

    @Test
    void verifyReturnsFalseForWrongPassword() {
        String stored = PasswordHasher.hash("Correct!Horse1");
        assertFalse(PasswordHasher.verify("WrongPassword!", stored));
    }

    @Test
    void verifyReturnsFalseForNullInputs() {
        String stored = PasswordHasher.hash("Correct!Horse1");
        assertFalse(PasswordHasher.verify(null, stored));
        assertFalse(PasswordHasher.verify("Correct!Horse1", null));
        assertFalse(PasswordHasher.verify(null, null));
    }

    @Test
    void verifyReturnsFalseForMalformedStoredValue() {
        assertFalse(PasswordHasher.verify("anything", "not-a-valid-hash"));
        assertFalse(PasswordHasher.verify("anything", ""));
        assertFalse(PasswordHasher.verify("anything", "no-colon-here"));
    }

    @Test
    void hashRejectsNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null));
    }
}
