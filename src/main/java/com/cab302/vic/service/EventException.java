package com.cab302.vic.service;

/** Thrown when event creation or editing fails validation. */
public class EventException extends Exception {
    public EventException(String message) {
        super(message);
    }
}
