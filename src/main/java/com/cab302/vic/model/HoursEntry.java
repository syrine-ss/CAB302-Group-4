package com.cab302.vic.model;

import java.util.Objects;

/**
 * Hours a volunteer has logged against an event they attended.
 *
 * <p>Every entry starts as {@link Status#PENDING}. The event's coordinator then
 * approves or rejects it, optionally with a note explaining the decision. Only
 * approved hours count towards a volunteer's total.
 */
public class HoursEntry {

    /** Where an entry is in the coordinator's review. */
    public enum Status {
        /** Logged by the volunteer, not yet reviewed. */
        PENDING,
        /** Accepted by the coordinator; counts towards the volunteer's total. */
        APPROVED,
        /** Declined by the coordinator; does not count. */
        REJECTED;

        /**
         * Reads a status stored in the database, treating a missing value as
         * pending so rows written before review existed still load.
         *
         * @param value the stored text, possibly null or blank
         * @return the matching status
         * @throws IllegalArgumentException if the text is not a known status
         */
        public static Status fromDb(String value) {
            if (value == null || value.isBlank()) {
                return PENDING;
            }
            return Status.valueOf(value.trim().toUpperCase());
        }
    }

    private int id;
    private final int userId;
    private final int eventId;
    private final double hours;
    private Status status;
    private String reviewNote;
    private final String loggedOn;

    /**
     * Creates an hours entry.
     *
     * @param id         the database id, or 0 if not yet saved
     * @param userId     the volunteer who logged the hours
     * @param eventId    the event the hours were worked at
     * @param hours      the number of hours worked
     * @param status     where the entry is in review
     * @param reviewNote the coordinator's note, or an empty string
     * @param loggedOn   the date the hours were logged, as YYYY-MM-DD
     */
    public HoursEntry(int id, int userId, int eventId, double hours,
                      Status status, String reviewNote, String loggedOn) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.hours = hours;
        this.status = status == null ? Status.PENDING : status;
        this.reviewNote = reviewNote == null ? "" : reviewNote;
        this.loggedOn = loggedOn;
    }

    /** @return the database id, or 0 if not yet saved */
    public int getId() { return id; }

    /** @return the id of the volunteer who logged the hours */
    public int getUserId() { return userId; }

    /** @return the id of the event the hours were worked at */
    public int getEventId() { return eventId; }

    /** @return the number of hours worked */
    public double getHours() { return hours; }

    /** @return where the entry is in review */
    public Status getStatus() { return status; }

    /** @return the coordinator's note, or an empty string if there is none */
    public String getReviewNote() { return reviewNote; }

    /** @return the date the hours were logged, as YYYY-MM-DD */
    public String getLoggedOn() { return loggedOn; }

    /** @return true while the entry is waiting for the coordinator */
    public boolean isPending() { return status == Status.PENDING; }

    /**
     * Set by the DAO once the row is inserted and a primary key is assigned.
     *
     * @param id the new database id
     */
    public void setId(int id) { this.id = id; }

    /**
     * Records the coordinator's decision.
     *
     * @param status     the new status
     * @param reviewNote an optional note, or null for none
     */
    public void review(Status status, String reviewNote) {
        this.status = status == null ? Status.PENDING : status;
        this.reviewNote = reviewNote == null ? "" : reviewNote.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoursEntry)) return false;
        HoursEntry other = (HoursEntry) o;
        return id == other.id && userId == other.userId && eventId == other.eventId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, eventId);
    }
}
