package com.cab302.vic.service;

import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.dao.HoursDAO;
import com.cab302.vic.dao.SignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for volunteers logging hours and coordinators reviewing them.
 *
 * <p>The rules:
 * <ul>
 *   <li>A volunteer can only log hours for an event the coordinator has
 *       marked them as attending.</li>
 *   <li>Hours must be more than 0 and no more than {@value #MAX_HOURS}.</li>
 *   <li>One active entry per volunteer per event: they can log again only
 *       if their previous entry was rejected.</li>
 *   <li>Only the event's coordinator can approve or reject, and only while
 *       the entry is pending. Rejecting needs a reason so the volunteer
 *       knows what to fix.</li>
 *   <li>Only approved hours count towards a volunteer's total.</li>
 * </ul>
 */
public class HoursService {

    /** The most hours that can be logged for a single event. */
    public static final double MAX_HOURS = 24.0;

    private final HoursDAO hoursDAO;
    private final SignupDAO signupDAO;
    private final EventDAO eventDAO;
    private final Clock clock;

    /**
     * Creates the service using the system clock
     * @param hoursDAO  storage for hours entries
     * @param signupDAO storage for signups, used to check attendance
     * @param eventDAO  storage for events, used to check who coordinates them
     */
    public HoursService(HoursDAO hoursDAO, SignupDAO signupDAO, EventDAO eventDAO) {
        this(hoursDAO, signupDAO, eventDAO, Clock.systemDefaultZone());
    }

    /**
     * Creates the service with a specific clock. Used by tests.
     * @param hoursDAO  storage for hours entries
     * @param signupDAO storage for signups
     * @param eventDAO  storage for events
     * @param clock     where "today" comes from, for the logged date
     */
    public HoursService(HoursDAO hoursDAO, SignupDAO signupDAO, EventDAO eventDAO, Clock clock) {
        this.hoursDAO = hoursDAO;
        this.signupDAO = signupDAO;
        this.eventDAO = eventDAO;
        this.clock = clock;
    }

    /**
     * Logs hours for an event the volunteer attended. The entry starts as pending.
     * @param eventId the event the hours were worked at
     * @param userId  the volunteer
     * @param hours   how many hours they worked
     * @return the new entry
     * @throws HoursException if the hours are out of range, the event doesn't
     *                        exist, they weren't marked as attending, or they
     *                        already have a pending or approved entry for it
     */
    public HoursEntry logHours(int eventId, int userId, double hours) throws HoursException {
        if (!(hours > 0) || hours > MAX_HOURS) {
            throw new HoursException("Hours must be more than 0 and no more than " + (int) MAX_HOURS);
        }
        findEvent(eventId);
        boolean attended = signupDAO.find(eventId, userId)
                .map(Signup::isAttended)
                .orElse(false);
        if (!attended) {
            throw new HoursException("You can only log hours for events you were marked as attending");
        }
        boolean alreadyActive = hoursDAO.findByUser(userId).stream()
                .anyMatch(e -> e.getEventId() == eventId && e.getStatus() != HoursEntry.Status.REJECTED);
        if (alreadyActive) {
            throw new HoursException("You have already logged hours for this event");
        }
        HoursEntry entry = new HoursEntry(0, userId, eventId, hours,
                HoursEntry.Status.PENDING, "", LocalDate.now(clock).toString());
        return hoursDAO.create(entry);
    }

    /**
     * Approves a pending entry.
     * @param entryId       the entry
     * @param coordinatorId the coordinator making the decision
     * @param note          an optional comment, or null
     * @return the updated entry
     * @throws HoursException if the entry doesn't exist, isn't pending, or the
     *                        coordinator didn't create the event
     */
    public HoursEntry approve(int entryId, int coordinatorId, String note) throws HoursException {
        return review(entryId, coordinatorId, HoursEntry.Status.APPROVED, note);
    }

    /**
     * Rejects a pending entry. A reason is required.
     * @param entryId       the entry
     * @param coordinatorId the coordinator making the decision
     * @param reason        why it was rejected, shown to the volunteer
     * @return the updated entry
     * @throws HoursException if no reason is given, the entry doesn't exist,
     *                        isn't pending, or the coordinator didn't create the event
     */
    public HoursEntry reject(int entryId, int coordinatorId, String reason) throws HoursException {
        if (reason == null || reason.isBlank()) {
            throw new HoursException("Please give a reason for rejecting these hours");
        }
        return review(entryId, coordinatorId, HoursEntry.Status.REJECTED, reason);
    }

    /**
     * Access the pending hours entries for a coordinator across all the coordinator's events, oldest first
     * @param coordinatorId the coordinator
     * @return list of pending entries
     */
    public List<HoursEntry> pendingForCoordinator(int coordinatorId) {
        List<HoursEntry> pending = new ArrayList<>();
        for (Event event : eventDAO.findByCoordinator(coordinatorId)) {
            for (HoursEntry entry : hoursDAO.findByEvent(event.getId())) {
                if (entry.isPending()) {
                    pending.add(entry);
                }
            }
        }
        pending.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        return pending;
    }

    /**
     * Every entry the volunteer has logged, with its review status
     * @param userId the volunteer
     * @return list of logged entries
     */
    public List<HoursEntry> entriesForVolunteer(int userId) {
        return hoursDAO.findByUser(userId);
    }

    /**
     * Get the total number approved hours for a given volunteer
     * @param userId the volunteer
     * @return the total of approved hours
     */
    public double totalApprovedHours(int userId) {
        return hoursDAO.findByUser(userId).stream()
                .filter(e -> e.getStatus() == HoursEntry.Status.APPROVED)
                .mapToDouble(HoursEntry::getHours)
                .sum();
    }

    // --- helpers ---

    private HoursEntry review(int entryId, int coordinatorId, HoursEntry.Status decision, String note)
            throws HoursException {
        HoursEntry entry = hoursDAO.findById(entryId)
                .orElseThrow(() -> new HoursException("Hours entry not found"));
        Event event = findEvent(entry.getEventId());
        if (event.getCreatedBy() != coordinatorId) {
            throw new HoursException("Only the event's coordinator can review these hours");
        }
        if (!entry.isPending()) {
            throw new HoursException("These hours have already been reviewed");
        }
        entry.review(decision, note);
        hoursDAO.updateReview(entry);
        return entry;
    }

    private Event findEvent(int eventId) throws HoursException {
        return eventDAO.findById(eventId)
                .orElseThrow(() -> new HoursException("Event not found"));
    }
}
