package com.cab302.vic.service;

/** Thrown when a signup or withdrawal is not allowed. */
public class SignupException extends Exception {
    public SignupException(String message) {
        super(message);
    }
}
