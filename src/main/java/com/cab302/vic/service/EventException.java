package com.cab302.vic.service;

/** Thrown when event creation or editing fails validation. */
public class EventException extends Exception {
    /**
     * Creates an event exception with the specified message
     * @param message the error message
     */
    public EventException(String message) {
        super(message);
    }
}
