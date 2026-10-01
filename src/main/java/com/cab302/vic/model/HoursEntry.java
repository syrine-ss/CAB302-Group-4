package com.cab302.vic.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Hours a volunteer claims for an event, and the coordinator's decision on them.
 *
 * <p>Entries start {@link Status#PENDING}. Only {@link Status#APPROVED} hours
 * count towards the impact totals, which is the whole point of the approval
 * step: reports shown to funders should only contain verified figures.
 */
public class HoursEntry {

    public enum Status { PENDING, APPROVED, REJECTED }

    private int id;
    private final int userId;
    private final int eventId;
    private double hours;
    private Status status;
    private String loggedOn;     // ISO-8601 date
    private String reviewNote;   // reason, required when rejecting

    public HoursEntry(int id, int userId, int eventId, double hours,
                      Status status, String loggedOn, String reviewNote) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.hours = hours;
        this.status = status;
        this.loggedOn = loggedOn;
        this.reviewNote = reviewNote;
    }

    /** Convenience for a fresh, unreviewed claim dated today. */
    public static HoursEntry createNew(int userId, int eventId, double hours) {
        return new HoursEntry(0, userId, eventId, hours,
                Status.PENDING, LocalDate.now().toString(), "");
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getEventId() { return eventId; }
    public double getHours() { return hours; }
    public Status getStatus() { return status; }
    public String getLoggedOn() { return loggedOn; }
    public String getReviewNote() { return reviewNote; }

    public void setId(int id) { this.id = id; }
    public void setHours(double hours) { this.hours = hours; }
    public void setStatus(Status status) { this.status = status; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }

    /** True when a coordinator has not yet made a decision on this claim. */
    public boolean isPending() {
        return status == Status.PENDING;
    }

    /** True only when the hours have been verified and should count in reports. */
    public boolean countsTowardsTotals() {
        return status == Status.APPROVED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoursEntry)) return false;
        return id == ((HoursEntry) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HoursEntry{user=" + userId + ", event=" + eventId
                + ", hours=" + hours + ", status=" + status + "}";
    }
}
