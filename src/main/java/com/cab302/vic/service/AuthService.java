package com.cab302.vic.service;

import com.cab302.vic.dao.UserDAO;
import com.cab302.vic.model.User;
import com.cab302.vic.util.PasswordHasher;

import java.util.regex.Pattern;

/**
 * Business logic for user registration and login.
 * Depends only on the {@link UserDAO} interface so it can be unit tested
 * against a fake DAO without touching the database.
 */
public class AuthService {

    // At least 8 chars, one uppercase, one special character (per team's user story #3)
    private static final Pattern PASSWORD_RULE =
            Pattern.compile("^(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$");

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Register a new user with a hashed password.
     * @throws AuthException when the username is taken or the password is too weak.
     */
    public User register(String username, String password, String fullName,
                         String email, User.Role role) throws AuthException {
        if (username == null || username.isBlank()) {
            throw new AuthException("Username is required");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new AuthException("Full name is required");
        }
        if (!isPasswordStrong(password)) {
            throw new AuthException(
                    "Password must be at least 8 characters and include one uppercase letter and one special character");
        }
        if (userDAO.existsByUsername(username)) {
            throw new AuthException("Username '" + username + "' is already taken");
        }

        String hash = PasswordHasher.hash(password);
        User user = new User(0, username, hash, fullName, email, role);
        return userDAO.create(user);
    }

    /**
     * Authenticate a user by username and password.
     * The error message is deliberately generic so it doesn't reveal
     * whether the username or the password was wrong.
     * @throws AuthException when credentials are invalid.
     */
    public User login(String username, String password) throws AuthException {
        if (username == null || password == null) {
            throw new AuthException("Invalid username or password");
        }
        User user = userDAO.findByUsername(username).orElse(null);
        if (user == null || !PasswordHasher.verify(password, user.getPasswordHash())) {
            throw new AuthException("Invalid username or password");
        }
        return user;
    }

    /** Exposed for tests and for UI hints. */
    public static boolean isPasswordStrong(String password) {
        return password != null && PASSWORD_RULE.matcher(password).matches();
    }
}
