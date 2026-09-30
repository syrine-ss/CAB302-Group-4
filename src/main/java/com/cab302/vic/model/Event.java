package com.cab302.vic.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a volunteer event created by a coordinator.
 * Dates are stored as ISO-8601 strings in SQLite; this class exposes them
 * both as strings (for FXML binding) and as {@link LocalDate}.
 *
 * <p>Prefer {@link #builder()} over the all-argument constructor. The
 * constructor takes four consecutive String parameters (description,
 * eventDate, eventTime, location), which the compiler cannot tell apart,
 * so swapping two of them produces a silent bug rather than an error.
 * The Builder names every value at the call site instead.
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

    // ------------------------------------------------------------------
    // Builder
    // ------------------------------------------------------------------

    /** Start building an Event with named values instead of positional ones. */
    public static Builder builder() {
        return new Builder();
    }

    /** Start from an existing event, for edit flows that change a few fields. */
    public static Builder builderFrom(Event source) {
        return new Builder()
                .id(source.id)
                .title(source.title)
                .description(source.description)
                .date(source.eventDate)
                .time(source.eventTime)
                .location(source.location)
                .volunteersNeeded(source.volunteersNeeded)
                .createdBy(source.createdBy);
    }

    /**
     * Fluent builder for {@link Event}.
     *
     * <p>Optional text fields default to an empty string rather than null,
     * so callers never have to null-check them when rendering.
     */
    public static class Builder {
        private int id = 0;
        private String title = "";
        private String description = "";
        private String eventDate = "";
        private String eventTime = "";
        private String location = "";
        private int volunteersNeeded = 1;
        private int createdBy = 0;

        public Builder id(int id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = orEmpty(description); return this; }
        public Builder date(String isoDate) { this.eventDate = orEmpty(isoDate); return this; }
        public Builder time(String time) { this.eventTime = orEmpty(time); return this; }
        public Builder location(String location) { this.location = orEmpty(location); return this; }
        public Builder volunteersNeeded(int n) { this.volunteersNeeded = n; return this; }
        public Builder createdBy(int userId) { this.createdBy = userId; return this; }

        public Event build() {
            return new Event(id, title, description, eventDate, eventTime,
                    location, volunteersNeeded, createdBy);
        }

        private static String orEmpty(String s) {
            return s == null ? "" : s;
        }
    }
}
