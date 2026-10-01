package com.cab302.vic.service;

import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.dao.SignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;

import java.time.LocalDate;
import java.util.List;

/**
 * Business rules for volunteers signing up to events, withdrawing, and
 * coordinators recording who actually turned up.
 *
 * <p>Depends on the DAO interfaces rather than the SQLite classes, so the
 * rules below are unit tested against in-memory fakes.
 */
public class SignupService {

    private final SignupDAO signupDAO;
    private final EventDAO eventDAO;

    public SignupService(SignupDAO signupDAO, EventDAO eventDAO) {
        this.signupDAO = signupDAO;
        this.eventDAO = eventDAO;
    }

    /**
     * Register a volunteer's interest in an event.
     *
     * @throws SignupException when the event does not exist, has already
     *         happened, is full, or the volunteer is already signed up.
     */
    public Signup signUp(int eventId, int volunteerId) throws SignupException {
        Event event = eventDAO.findById(eventId)
                .orElseThrow(() -> new SignupException("That event no longer exists"));

        if (hasPassed(event)) {
            throw new SignupException("You cannot sign up to an event that has already happened");
        }
        if (signupDAO.find(eventId, volunteerId).isPresent()) {
            throw new SignupException("You are already signed up to this event");
        }
        if (isFull(event)) {
            throw new SignupException("This event is already full");
        }

        return signupDAO.create(Signup.createNew(eventId, volunteerId));
    }

    /**
     * Cancel a volunteer's signup, freeing the spot for someone else.
     *
     * @throws SignupException when there is no signup to cancel, or the
     *         coordinator has already recorded them as having attended.
     */
    public void withdraw(int eventId, int volunteerId) throws SignupException {
        Signup signup = signupDAO.find(eventId, volunteerId)
                .orElseThrow(() -> new SignupException("You are not signed up to this event"));

        if (signup.isAttended()) {
            throw new SignupException(
                    "You cannot withdraw from an event you have been marked as attending");
        }
        signupDAO.delete(eventId, volunteerId);
    }

    /**
     * Record whether a volunteer turned up. Only meaningful once the event
     * is under way, so calls before the day of the event are rejected.
     *
     * @throws SignupException when the event is still in the future or the
     *         volunteer was never signed up.
     */
    public Signup markAttendance(int eventId, int volunteerId, boolean attended)
            throws SignupException {
        // Allowed from the day of the event onwards: see canRecordAttendance.
        Event event = eventDAO.findById(eventId)
                .orElseThrow(() -> new SignupException("That event no longer exists"));

        if (!canRecordAttendance(event)) {
            throw new SignupException(
                    "Attendance can only be recorded on or after the day of the event");
        }

        Signup signup = signupDAO.find(eventId, volunteerId)
                .orElseThrow(() -> new SignupException("That volunteer is not signed up to this event"));

        signup.setAttended(attended);
        signupDAO.update(signup);
        return signup;
    }

    public List<Signup> findByEvent(int eventId) {
        return signupDAO.findByEvent(eventId);
    }

    public List<Signup> findByVolunteer(int volunteerId) {
        return signupDAO.findByUser(volunteerId);
    }

    public boolean isSignedUp(int eventId, int volunteerId) {
        return signupDAO.find(eventId, volunteerId).isPresent();
    }

    public int countForEvent(int eventId) {
        return signupDAO.countForEvent(eventId);
    }

    /** Remaining places, never negative. */
    public int spotsRemaining(Event event) {
        int taken = signupDAO.countForEvent(event.getId());
        return Math.max(0, event.getVolunteersNeeded() - taken);
    }

    public boolean isFull(Event event) {
        return signupDAO.countForEvent(event.getId()) >= event.getVolunteersNeeded();
    }

    /**
     * True once the event date is in the past.
     *
     * <p>An event happening today is still open for signups: someone can
     * decide to come along to this afternoon's working bee.
     */
    static boolean hasPassed(Event event) {
        LocalDate date = event.parsedDate();
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * True once the event is under way or finished, meaning its date is
     * today or earlier.
     *
     * <p>Deliberately a different rule from {@link #hasPassed}. Attendance
     * is taken at the event itself, so a coordinator running a working bee
     * this morning should be able to mark the register that afternoon
     * rather than waiting until the next day. Signups use the stricter
     * rule, because an event today can still accept volunteers.
     */
    static boolean canRecordAttendance(Event event) {
        LocalDate date = event.parsedDate();
        return date != null && !date.isAfter(LocalDate.now());
    }
}
