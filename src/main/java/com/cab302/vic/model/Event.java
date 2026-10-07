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

    /**
     * Creates an event from every field at once.
     *
     * <p>Prefer {@link #builder()}: with eight positional arguments, five of
     * them strings, it is easy to pass the date and time in the wrong order
     * without the compiler noticing. This constructor is kept so existing code
     * and tests keep working.
     *
     * @param id               the database id, or 0 if not yet saved
     * @param title            the event title
     * @param description      what volunteers will be doing
     * @param eventDate        the date, as YYYY-MM-DD
     * @param eventTime        the start time, as HH:mm
     * @param location         where the event is held
     * @param volunteersNeeded how many volunteers the coordinator wants
     * @param createdBy        the id of the coordinator who created it
     */
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

    /**
     * Starts building an event one named field at a time.
     *
     * <p>Example:
     * <pre>{@code
     * Event event = Event.builder()
     *         .title("Beach clean-up")
     *         .date("2026-10-20")
     *         .time("09:00")
     *         .location("Manly Beach")
     *         .volunteersNeeded(10)
     *         .createdBy(coordinatorId)
     *         .build();
     * }</pre>
     *
     * @return a new builder with every optional field empty
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds an {@link Event} with named steps instead of a long positional
     * constructor (Builder pattern).
     *
     * <p>Optional text fields default to an empty string rather than null, so
     * the rest of the app never has to null-check them. A title and a date are
     * required; {@link #build()} refuses to create an event without them.
     */
    public static final class Builder {

        private int id;
        private String title;
        private String description = "";
        private String eventDate;
        private String eventTime = "";
        private String location = "";
        private int volunteersNeeded;
        private int createdBy;

        private Builder() {
        }

        /**
         * @param id the database id; leave unset for a new, unsaved event
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * @param title the event title (required)
         * @return this builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * @param description what volunteers will be doing; null is stored as empty
         * @return this builder
         */
        public Builder description(String description) {
            this.description = orEmpty(description);
            return this;
        }

        /**
         * @param eventDate the date as YYYY-MM-DD (required)
         * @return this builder
         */
        public Builder date(String eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        /**
         * @param eventTime the start time as HH:mm; null is stored as empty
         * @return this builder
         */
        public Builder time(String eventTime) {
            this.eventTime = orEmpty(eventTime);
            return this;
        }

        /**
         * @param location where the event is held; null is stored as empty
         * @return this builder
         */
        public Builder location(String location) {
            this.location = orEmpty(location);
            return this;
        }

        /**
         * @param volunteersNeeded how many volunteers the coordinator wants
         * @return this builder
         */
        public Builder volunteersNeeded(int volunteersNeeded) {
            this.volunteersNeeded = volunteersNeeded;
            return this;
        }

        /**
         * @param createdBy the id of the coordinator who created the event
         * @return this builder
         */
        public Builder createdBy(int createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        /**
         * Creates the event.
         *
         * <p>Only checks that the event is complete enough to exist. Business
         * rules such as "no events in the past" stay in {@code EventService}.
         *
         * @return the new event
         * @throws IllegalStateException if the title or date is missing
         */
        public Event build() {
            if (title == null || title.isBlank()) {
                throw new IllegalStateException("An event needs a title");
            }
            if (eventDate == null || eventDate.isBlank()) {
                throw new IllegalStateException("An event needs a date");
            }
            return new Event(id, title, description, eventDate, eventTime,
                    location, volunteersNeeded, createdBy);
        }

        private static String orEmpty(String value) {
            return value == null ? "" : value;
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
