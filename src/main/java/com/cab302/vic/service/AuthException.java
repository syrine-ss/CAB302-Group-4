package com.cab302.vic.service;

/**
 * Thrown when authentication or registration fails for a reason the user
 * should see (e.g. wrong password, weak password, duplicate username).
 * Uses a checked exception so callers must handle it deliberately.
 */
public class AuthException extends Exception {

    /**
     * Creates an authentication exception with the specified message
     * @param message the error message
     */
    public AuthException(String message) {
        super(message);
    }
}
