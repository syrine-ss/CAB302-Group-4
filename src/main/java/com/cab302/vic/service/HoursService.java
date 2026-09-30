package com.cab302.vic.service;

import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.dao.HoursDAO;
import com.cab302.vic.dao.SignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;

import java.util.List;

/**
 * Business rules for logging volunteer hours and for coordinators approving
 * or rejecting them.
 *
 * <p>The approval step exists so impact reports only ever contain verified
 * figures. Every rule here protects that: hours cannot be claimed for events
 * the volunteer did not attend, cannot be edited once a decision is made,
 * and can only be reviewed by the coordinator who created the event.
 */
public class HoursService {

    /** A sanity ceiling. Nobody volunteers more than a full day at one event. */
    public static final double MAX_HOURS_PER_EVENT = 24.0;

    private final HoursDAO hoursDAO;
    private final SignupDAO signupDAO;
    private final EventDAO eventDAO;

    public HoursService(HoursDAO hoursDAO, SignupDAO signupDAO, EventDAO eventDAO) {
        this.hoursDAO = hoursDAO;
        this.signupDAO = signupDAO;
        this.eventDAO = eventDAO;
    }

    /**
     * Claim hours for an event the volunteer attended.
     *
     * @throws HoursException when the amount is out of range, the volunteer
     *         was not marked as attending, or they have already claimed.
     */
    public HoursEntry logHours(int volunteerId, int eventId, double hours) throws HoursException {
        validateAmount(hours);

        eventDAO.findById(eventId)
                .orElseThrow(() -> new HoursException("That event no longer exists"));

        Signup signup = signupDAO.find(eventId, volunteerId)
                .orElseThrow(() -> new HoursException(
                        "You can only log hours for events you signed up to"));

        if (!signup.isAttended()) {
            throw new HoursException(
                    "The coordinator has not yet marked you as having attended this event");
        }
        if (hoursDAO.find(volunteerId, eventId).isPresent()) {
            throw new HoursException(
                    "You have already logged hours for this event. Edit the existing entry instead.");
        }

        return hoursDAO.create(HoursEntry.createNew(volunteerId, eventId, hours));
    }

    /**
     * Change an hours claim that has not been reviewed yet.
     *
     * @throws HoursException when the entry does not exist, belongs to
     *         someone else, or has already been approved or rejected.
     */
    public HoursEntry updateHours(int entryId, int volunteerId, double hours) throws HoursException {
        validateAmount(hours);

        HoursEntry entry = hoursDAO.findById(entryId)
                .orElseThrow(() -> new HoursException("That hours entry no longer exists"));

        if (entry.getUserId() != volunteerId) {
            throw new HoursException("You can only edit your own hours");
        }
        if (!entry.isPending()) {
            throw new HoursException(
                    "These hours have already been reviewed and can no longer be changed");
        }

        entry.setHours(hours);
        hoursDAO.update(entry);
        return entry;
    }

    /**
     * Approve a claim so it counts towards impact totals.
     *
     * @throws HoursException when the reviewer did not create the event, or
     *         a decision has already been made.
     */
    public HoursEntry approve(int entryId, int coordinatorId) throws HoursException {
        HoursEntry entry = loadReviewable(entryId, coordinatorId);
        entry.setStatus(HoursEntry.Status.APPROVED);
        entry.setReviewNote("");
        hoursDAO.update(entry);
        return entry;
    }

    /**
     * Reject a claim, recording why so the volunteer understands the decision.
     *
     * @throws HoursException when no reason is given, the reviewer did not
     *         create the event, or a decision has already been made.
     */
    public HoursEntry reject(int entryId, int coordinatorId, String reason) throws HoursException {
        if (reason == null || reason.isBlank()) {
            throw new HoursException("Please give a reason so the volunteer understands why");
        }
        HoursEntry entry = loadReviewable(entryId, coordinatorId);
        entry.setStatus(HoursEntry.Status.REJECTED);
        entry.setReviewNote(reason.trim());
        hoursDAO.update(entry);
        return entry;
    }

    public List<HoursEntry> findPendingForCoordinator(int coordinatorId) {
        return hoursDAO.findPendingForCoordinator(coordinatorId);
    }

    public List<HoursEntry> findByVolunteer(int volunteerId) {
        return hoursDAO.findByUser(volunteerId);
    }

    /** Verified hours only, which is what reports to funders should show. */
    public double totalApprovedHoursForCoordinator(int coordinatorId) {
        return hoursDAO.totalApprovedHoursForCoordinator(coordinatorId);
    }

    /** A volunteer's own running total of approved hours. */
    public double totalApprovedHoursForVolunteer(int volunteerId) {
        return hoursDAO.findByUser(volunteerId).stream()
                .filter(HoursEntry::countsTowardsTotals)
                .mapToDouble(HoursEntry::getHours)
                .sum();
    }

    // --- internals ---

    /** Load an entry, confirming it is still pending and the reviewer owns the event. */
    private HoursEntry loadReviewable(int entryId, int coordinatorId) throws HoursException {
        HoursEntry entry = hoursDAO.findById(entryId)
                .orElseThrow(() -> new HoursException("That hours entry no longer exists"));

        if (!entry.isPending()) {
            throw new HoursException("These hours have already been reviewed");
        }

        Event event = eventDAO.findById(entry.getEventId())
                .orElseThrow(() -> new HoursException("The event for these hours no longer exists"));

        if (event.getCreatedBy() != coordinatorId) {
            throw new HoursException("You can only review hours for events you created");
        }
        return entry;
    }

    private static void validateAmount(double hours) throws HoursException {
        if (hours <= 0) {
            throw new HoursException("Hours must be greater than zero");
        }
        if (hours > MAX_HOURS_PER_EVENT) {
            throw new HoursException("Hours cannot exceed " + (int) MAX_HOURS_PER_EVENT
                    + " for a single event");
        }
    }
}
