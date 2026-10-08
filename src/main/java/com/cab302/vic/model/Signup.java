package com.cab302.vic.model;

import java.util.Objects;

/**
 * A volunteer's place on an event, and whether they turned up.
 *
 * <p>A signup is created when a volunteer joins an event and removed if they
 * withdraw. After the event, the coordinator marks it as attended, which is
 * what allows the volunteer to log hours for that event.
 */
public class Signup {

    private int id;
    private final int eventId;
    private final int userId;
    private boolean attended;
    private final String signedUpOn;

    /**
     * Creates a signup.
     *
     * @param id         the database id, or 0 if not yet saved
     * @param eventId    the event signed up for
     * @param userId     the volunteer who signed up
     * @param attended   whether the coordinator has marked them as attended
     * @param signedUpOn the date of signing up, as YYYY-MM-DD
     */
    public Signup(int id, int eventId, int userId, boolean attended, String signedUpOn) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.attended = attended;
        this.signedUpOn = signedUpOn;
    }

    /**
     * Return the signup id
     * @return the signup database id, or 0 if not yet saved
     */
    public int getId() { return id; }

    /**
     * Return the event id of a signed up event
     * @return the id of the event signed up for 
     */
    public int getEventId() { return eventId; }

    /**
     * Return the id of a signed up volunteer
     * @return the id of the volunteer who signed up 
     */
    public int getUserId() { return userId; }

    /**
     * Return whether the coordinator has marked the volunteer as attended
     * @return true if set to attended
     */
    public boolean isAttended() { return attended; }

    /**
     * Return the date of signing up
     * @return the signup date, as YYYY-MM-DD
     */
    public String getSignedUpOn() { return signedUpOn; }

    /**
     * Set by the DAO once the row is inserted and a primary key is assigned.
     *
     * @param id the new database id
     */
    public void setId(int id) { this.id = id; }

    /**
     * Records whether the volunteer attended the event.
     *
     * @param attended true if they attended
     */
    public void setAttended(boolean attended) { this.attended = attended; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Signup)) return false;
        Signup other = (Signup) o;
        return id == other.id && eventId == other.eventId && userId == other.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, userId);
    }
}
