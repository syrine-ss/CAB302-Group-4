package com.cab302.vic.service;

/** Thrown when logging or reviewing volunteer hours is not allowed. */
public class HoursException extends Exception {
    public HoursException(String message) {
        super(message);
    }
}
