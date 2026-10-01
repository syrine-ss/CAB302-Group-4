package com.cab302.vic.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A volunteer's registration of interest in an event.
 *
 * <p>A signup is deliberately kept separate from attendance. A volunteer
 * signs up ahead of time, and the coordinator marks attendance afterwards,
 * so the two facts can disagree (someone signs up and then does not show).
 * Hours can only be logged once attendance has been confirmed.
 */
public class Signup {

    private int id;
    private final int eventId;
    private final int userId;
    private String signedUpOn;   // ISO-8601 date, so SQLite can sort it as text
    private boolean attended;

    public Signup(int id, int eventId, int userId, String signedUpOn, boolean attended) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.signedUpOn = signedUpOn;
        this.attended = attended;
    }

    /** Convenience for creating a brand new signup dated today. */
    public static Signup createNew(int eventId, int userId) {
        return new Signup(0, eventId, userId, LocalDate.now().toString(), false);
    }

    public int getId() { return id; }
    public int getEventId() { return eventId; }
    public int getUserId() { return userId; }
    public String getSignedUpOn() { return signedUpOn; }
    public boolean isAttended() { return attended; }

    public void setId(int id) { this.id = id; }
    public void setAttended(boolean attended) { this.attended = attended; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Signup)) return false;
        Signup other = (Signup) o;
        // Identity is the pairing, not the row id: a volunteer can only be
        // signed up to a given event once.
        return eventId == other.eventId && userId == other.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId);
    }

    @Override
    public String toString() {
        return "Signup{event=" + eventId + ", user=" + userId + ", attended=" + attended + "}";
    }
}
