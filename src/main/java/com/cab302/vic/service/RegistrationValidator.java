package com.cab302.vic.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Checks a registration form before it is sent to {@link AuthService}.
 * Every problem is reported at once, keyed by the field it belongs to,
 * so the screen can show each message next to the right field instead of
 * one message at a time at the bottom of the form.
 * Has no JavaFX dependency so it can be unit tested directly.
 */
public final class RegistrationValidator {

    /** The fields on the registration form, in the order they appear. */
    public enum Field { USERNAME, FULL_NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

    private RegistrationValidator() {}

    /**
     * Validates every field of the registration form.
     *
     * @param username the username entered
     * @param fullName the full name entered
     * @param email the email entered
     * @param password the password entered
     * @param confirmPassword the repeated password
     * @return a map from each invalid field to its error message, in form order;
     *         empty when the form is valid
     */
    public static Map<Field, String> validate(String username, String fullName, String email,
                                              String password, String confirmPassword) {
        Map<Field, String> errors = new LinkedHashMap<>();
        if (isBlank(username)) {
            errors.put(Field.USERNAME, "Enter a username.");
        }
        if (isBlank(fullName)) {
            errors.put(Field.FULL_NAME, "Enter your full name.");
        }
        if (isBlank(email)) {
            errors.put(Field.EMAIL, "Enter your email address.");
        } else if (!AuthService.isEmailValid(email.trim())) {
            errors.put(Field.EMAIL, "Enter a valid email address, like name@example.com.");
        }
        if (isBlank(password)) {
            errors.put(Field.PASSWORD, "Enter a password.");
        } else if (!AuthService.isPasswordStrong(password)) {
            errors.put(Field.PASSWORD, "Password doesn't meet the requirements below.");
        }
        if (!isBlank(password) && !password.equals(confirmPassword)) {
            errors.put(Field.CONFIRM_PASSWORD, "Passwords don't match.");
        }
        return errors;
    }

    /**
     * Builds the live password checklist shown under the password field,
     * marking each rule as met or not met.
     *
     * @param password the password typed so far (may be null)
     * @return a one-line checklist, with a tick or a cross in front of each rule
     */
    public static String passwordChecklist(String password) {
        String p = password == null ? "" : password;
        boolean longEnough = p.length() >= 8;
        // Same rules as AuthService.PASSWORD_RULE: A-Z for uppercase, anything
        // other than A-Z, a-z or 0-9 counts as a special character.
        boolean hasUpper = p.chars().anyMatch(c -> c >= 'A' && c <= 'Z');
        boolean hasSpecial = p.chars().anyMatch(c -> !((c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')));
        return mark(longEnough) + " 8+ characters   "
                + mark(hasUpper) + " 1 uppercase letter   "
                + mark(hasSpecial) + " 1 special character";
    }

    private static String mark(boolean ok) {
        return ok ? "\u2713" : "\u2717"; // tick / cross
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
