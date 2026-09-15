package com.cab302.vic.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a volunteer event created by a coordinator.
 * Dates are stored as ISO-8601 strings in SQLite; this class exposes them
 * both as strings (for FXML binding) and as {@link LocalDate}.
 */
public class Event {

    private int id;
    private String title;
    private String description;
    private String eventDate;      // ISO-8601 (YYYY-MM-DD) so SQLite comparisons work
    private String eventTime;      // HH:mm
    private String location;
    private int volunteersNeeded;
    private int createdBy;

    public Event(int id, String title, String description,
                 String eventDate, String eventTime, String location,
                 int volunteersNeeded, int createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.volunteersNeeded = volunteersNeeded;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getEventDate() { return eventDate; }
    public String getEventTime() { return eventTime; }
    public String getLocation() { return location; }
    public int getVolunteersNeeded() { return volunteersNeeded; }
    public int getCreatedBy() { return createdBy; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public void setLocation(String location) { this.location = location; }
    public void setVolunteersNeeded(int volunteersNeeded) { this.volunteersNeeded = volunteersNeeded; }

    /** Parse the stored eventDate as a LocalDate. Returns null when the string can't be parsed. */
    public LocalDate parsedDate() {
        try {
            return LocalDate.parse(eventDate);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
