package com.cab302.vic.service;

import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for creating, editing, and listing events.
 * Enforces the rules that live outside the database schema (e.g. "can't
 * create an event in the past") so the UI stays thin.
 */
public class EventService {

    private final EventDAO eventDAO;

    public EventService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    /** Create a new event after validating required fields and date. */
    public Event create(String title, String description, String eventDate, String eventTime,
                        String location, int volunteersNeeded, int createdBy) throws EventException {
        validateFields(title, eventDate, volunteersNeeded);
        assertNotInPast(eventDate);

        Event event = new Event(0, title.trim(), safe(description), eventDate,
                safe(eventTime), safe(location), volunteersNeeded, createdBy);
        return eventDAO.create(event);
    }

    /** Update an existing event. Same validation rules as create. */
    public Event update(int eventId, String title, String description, String eventDate, String eventTime,
                        String location, int volunteersNeeded) throws EventException {
        Event existing = eventDAO.findById(eventId)
                .orElseThrow(() -> new EventException("Event not found"));

        validateFields(title, eventDate, volunteersNeeded);
        assertNotInPast(eventDate);

        existing.setTitle(title.trim());
        existing.setDescription(safe(description));
        existing.setEventDate(eventDate);
        existing.setEventTime(safe(eventTime));
        existing.setLocation(safe(location));
        existing.setVolunteersNeeded(volunteersNeeded);

        boolean updated = eventDAO.update(existing);
        if (!updated) {
            throw new EventException("Failed to save changes");
        }
        return existing;
    }

    public List<Event> findAll() {
        return eventDAO.findAll();
    }

    public List<Event> findByCoordinator(int coordinatorId) {
        return eventDAO.findByCoordinator(coordinatorId);
    }

    public Optional<Event> findById(int id) {
        return eventDAO.findById(id);
    }

    // --- validation ---

    private static void validateFields(String title, String eventDate, int volunteersNeeded)
            throws EventException {
        if (title == null || title.isBlank()) {
            throw new EventException("Event title is required");
        }
        if (eventDate == null || eventDate.isBlank()) {
            throw new EventException("Event date is required");
        }
        if (volunteersNeeded < 1) {
            throw new EventException("Volunteers needed must be at least 1");
        }
    }

    private static void assertNotInPast(String isoDate) throws EventException {
        try {
            LocalDate date = LocalDate.parse(isoDate);
            if (date.isBefore(LocalDate.now())) {
                throw new EventException("Event date cannot be in the past");
            }
        } catch (java.time.format.DateTimeParseException e) {
            throw new EventException("Event date must be in YYYY-MM-DD format");
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
