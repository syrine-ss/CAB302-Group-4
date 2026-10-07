package com.cab302.vic.service;

/** Thrown when logging or reviewing hours is not allowed. */
public class HoursException extends Exception {

    /**
     * @param message a reason the UI can show to the user
     */
    public HoursException(String message) {
        super(message);
    }
}
