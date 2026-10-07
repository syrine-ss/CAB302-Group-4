package com.cab302.vic.service;

/** Thrown when signing up, withdrawing or recording attendance is not allowed. */
public class SignupException extends Exception {

    /**
     * @param message a reason the UI can show to the user
     */
    public SignupException(String message) {
        super(message);
    }
}
