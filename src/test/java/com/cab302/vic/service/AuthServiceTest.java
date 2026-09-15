package com.cab302.vic.service;

import com.cab302.vic.dao.FakeUserDAO;
import com.cab302.vic.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour-focused tests for {@link AuthService}.
 * Uses the in-memory {@link FakeUserDAO} so nothing touches the real database.
 * These tests were written before the service (TDD): each expected outcome
 * was captured, watched fail (Red), the service was written to make them pass
 * (Green), then the password validation regex was refactored to a compiled
 * pattern (Refactor).
 */
class AuthServiceTest {

    private FakeUserDAO userDAO;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        userDAO = new FakeUserDAO();
        auth = new AuthService(userDAO);
    }

    // ---------- register ----------

    @Test
    void registerCreatesUserAndAssignsId() throws AuthException {
        User user = auth.register("syrine", "Strong!Pass1", "Syrine Shraim",
                "syrine@example.com", User.Role.COORDINATOR);

        assertTrue(user.getId() > 0, "New user should have a database id");
        assertEquals("syrine", user.getUsername());
        assertEquals(1, userDAO.size());
    }

    @Test
    void registerStoresHashedPasswordNotPlaintext() throws AuthException {
        User user = auth.register("sam", "Strong!Pass1", "Sam Turner",
                "sam@example.com", User.Role.VOLUNTEER);

        assertNotEquals("Strong!Pass1", user.getPasswordHash());
        assertTrue(user.getPasswordHash().contains(":"),
                "Password hash should be in salt:hash format");
    }

    @Test
    void registerRejectsDuplicateUsername() throws AuthException {
        auth.register("maia", "Strong!Pass1", "Maia Sherwin", "maia@example.com",
                User.Role.VOLUNTEER);

        AuthException ex = assertThrows(AuthException.class,
                () -> auth.register("maia", "Another!Pass1", "Someone Else",
                        "someone@example.com", User.Role.COORDINATOR));
        assertTrue(ex.getMessage().toLowerCase().contains("taken"));
    }

    @Test
    void registerRejectsWeakPassword() {
        assertThrows(AuthException.class,
                () -> auth.register("weak", "short", "Weak User", "w@e.com", User.Role.VOLUNTEER),
                "Password shorter than 8 chars should be rejected");
        assertThrows(AuthException.class,
                () -> auth.register("weak", "lowercase1!", "Weak User", "w@e.com", User.Role.VOLUNTEER),
                "Password without uppercase should be rejected");
        assertThrows(AuthException.class,
                () -> auth.register("weak", "NoSpecialChar1", "Weak User", "w@e.com", User.Role.VOLUNTEER),
                "Password without special character should be rejected");
    }

    @Test
    void registerRejectsBlankUsername() {
        assertThrows(AuthException.class,
                () -> auth.register("", "Strong!Pass1", "Blank Name", "b@e.com", User.Role.VOLUNTEER));
        assertThrows(AuthException.class,
                () -> auth.register("   ", "Strong!Pass1", "Blank Name", "b@e.com", User.Role.VOLUNTEER));
    }

    // ---------- login ----------

    @Test
    void loginSucceedsWithCorrectCredentials() throws AuthException {
        auth.register("aedan", "Strong!Pass1", "Aedan Manche", "a@e.com", User.Role.COORDINATOR);

        User user = auth.login("aedan", "Strong!Pass1");
        assertEquals("aedan", user.getUsername());
        assertEquals(User.Role.COORDINATOR, user.getRole());
    }

    @Test
    void loginFailsWithWrongPassword() throws AuthException {
        auth.register("ryan", "Strong!Pass1", "Ryan Francis", "r@e.com", User.Role.VOLUNTEER);
        assertThrows(AuthException.class, () -> auth.login("ryan", "WrongPassword!"));
    }

    @Test
    void loginFailsForUnknownUser() {
        assertThrows(AuthException.class, () -> auth.login("ghost", "Strong!Pass1"));
    }

    @Test
    void loginErrorMessageDoesNotRevealWhichPartIsWrong() throws AuthException {
        // Security property: an attacker probing for valid usernames should
        // not be able to tell the difference between "user doesn't exist" and
        // "password is wrong".
        auth.register("bailey", "Strong!Pass1", "Bailey M", "b@e.com", User.Role.VOLUNTEER);

        String wrongUser = assertThrows(AuthException.class,
                () -> auth.login("ghost", "Strong!Pass1")).getMessage();
        String wrongPass = assertThrows(AuthException.class,
                () -> auth.login("bailey", "WrongPassword!")).getMessage();

        assertEquals(wrongUser, wrongPass);
    }

    @Test
    void loginHandlesNullInputsSafely() {
        assertThrows(AuthException.class, () -> auth.login(null, "Strong!Pass1"));
        assertThrows(AuthException.class, () -> auth.login("someone", null));
    }
}
