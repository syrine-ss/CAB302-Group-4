package com.cab302.vic.service;

import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.dao.SignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Business rules for volunteers signing up to events and coordinators
 * recording who attended.
 *
 * <p>The rules:
 * <ul>
 *   <li>A volunteer can sign up for an event today or later, once, while
 *       there are spots left.</li>
 *   <li>They can withdraw until the event has happened, unless their
 *       attendance has already been recorded.</li>
 *   <li>Only the coordinator who created an event can record attendance,
 *       and only on or after the day of the event.</li>
 * </ul>
 *
 * <p>"Today" comes from a {@link Clock} so tests can fix the date instead of
 * depending on when they run.
 */
public class SignupService {

    private final SignupDAO signupDAO;
    private final EventDAO eventDAO;
    private final Clock clock;

    /**
     * Creates the service using the system clock.
     *
     * @param signupDAO storage for signups
     * @param eventDAO  storage for events, used to check dates and capacity
     */
    public SignupService(SignupDAO signupDAO, EventDAO eventDAO) {
        this(signupDAO, eventDAO, Clock.systemDefaultZone());
    }

    /**
     * Creates the service with a specific clock. Used by tests.
     *
     * @param signupDAO storage for signups
     * @param eventDAO  storage for events
     * @param clock     where "today" comes from
     */
    public SignupService(SignupDAO signupDAO, EventDAO eventDAO, Clock clock) {
        this.signupDAO = signupDAO;
        this.eventDAO = eventDAO;
        this.clock = clock;
    }

    /**
     * Signs a volunteer up for an event.
     *
     * @param eventId the event to join
     * @param userId  the volunteer
     * @return the new signup
     * @throws SignupException if the event doesn't exist, has already
     *                         happened, is full, or they are already signed up
     */
    public Signup signUp(int eventId, int userId) throws SignupException {
        Event event = findEvent(eventId);
        if (isBeforeToday(event)) {
            throw new SignupException("This event has already happened");
        }
        if (signupDAO.find(eventId, userId).isPresent()) {
            throw new SignupException("You are already signed up for this event");
        }
        if (spotsLeft(event) <= 0) {
            throw new SignupException("This event is full");
        }
        Signup signup = new Signup(0, eventId, userId, false, today().toString());
        return signupDAO.create(signup);
    }

    /**
     * Removes a volunteer from an event.
     *
     * @param eventId the event to leave
     * @param userId  the volunteer
     * @throws SignupException if they aren't signed up, the event has already
     *                         happened, or their attendance has been recorded
     */
    public void withdraw(int eventId, int userId) throws SignupException {
        Event event = findEvent(eventId);
        Signup signup = signupDAO.find(eventId, userId)
                .orElseThrow(() -> new SignupException("You are not signed up for this event"));
        if (signup.isAttended()) {
            throw new SignupException("Your attendance has already been recorded for this event");
        }
        if (isBeforeToday(event)) {
            throw new SignupException("You can't withdraw from an event that has already happened");
        }
        signupDAO.delete(eventId, userId);
    }

    /**
     * Records whether a volunteer attended an event.
     *
     * @param eventId       the event
     * @param userId        the volunteer
     * @param attended      true if they attended
     * @param coordinatorId the coordinator making the change
     * @throws SignupException if the coordinator didn't create the event, the
     *                         event hasn't started yet, or the volunteer isn't
     *                         signed up
     */
    public void setAttendance(int eventId, int userId, boolean attended, int coordinatorId)
            throws SignupException {
        Event event = findEvent(eventId);
        if (event.getCreatedBy() != coordinatorId) {
            throw new SignupException("Only the event's coordinator can record attendance");
        }
        if (!canRecordAttendance(event)) {
            throw new SignupException("Attendance can be recorded from the day of the event");
        }
        if (!signupDAO.setAttended(eventId, userId, attended)) {
            throw new SignupException("That volunteer is not signed up for this event");
        }
    }

    /**
     * @param eventId the event
     * @param userId  the volunteer
     * @return true if the volunteer is signed up for the event
     */
    public boolean isSignedUp(int eventId, int userId) {
        return signupDAO.find(eventId, userId).isPresent();
    }

    /**
     * @param eventId the event
     * @return everyone signed up for the event, for the attendance register
     */
    public List<Signup> signupsForEvent(int eventId) {
        return signupDAO.findByEvent(eventId);
    }

    /**
     * @param userId the volunteer
     * @return every event the volunteer is signed up for
     */
    public List<Signup> signupsForVolunteer(int userId) {
        return signupDAO.findByUser(userId);
    }

    /**
     * @param event the event
     * @return how many more volunteers can sign up, never below zero
     */
    public int spotsLeft(Event event) {
        return Math.max(0, event.getVolunteersNeeded() - signupDAO.countForEvent(event.getId()));
    }

    /**
     * Attendance can be marked from the day of the event onwards, so a
     * coordinator can do it the same afternoon.
     *
     * @param event the event
     * @return true if the event is today or earlier
     */
    public boolean canRecordAttendance(Event event) {
        LocalDate date = event.parsedDate();
        return date != null && !date.isAfter(today());
    }

    // --- helpers ---

    private Event findEvent(int eventId) throws SignupException {
        return eventDAO.findById(eventId)
                .orElseThrow(() -> new SignupException("Event not found"));
    }

    /** Signups stay open on the day itself, so only earlier dates count as passed. */
    private boolean isBeforeToday(Event event) {
        LocalDate date = event.parsedDate();
        return date != null && date.isBefore(today());
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
