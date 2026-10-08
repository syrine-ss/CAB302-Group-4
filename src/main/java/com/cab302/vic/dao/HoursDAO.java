package com.cab302.vic.dao;

import com.cab302.vic.model.HoursEntry;

import java.util.List;
import java.util.Optional;

/** DAO contract for hours that volunteers log against events. */
public interface HoursDAO {

    /**
     * Insert hours logged by a volunteer into database
     * @param entry the entry hours logged by volunteer
     * @return the entry inputted
     */
    HoursEntry create(HoursEntry entry);

    /**
     * Find hours logged in database using its id
     * @param id the entry id
     * @return list hours logged by that id, empty if none found
     */
    Optional<HoursEntry> findById(int id);

    /**
     * Find hours logged in database using the relevant event id
     * @param eventId the event
     * @return list of hours logged for that event id if any found, oldest first
     */
    List<HoursEntry> findByEvent(int eventId);

    /**
     * Find hours entry by a user id
     * @param userId the volunteer
     * @return every entry the volunteer has logged, oldest first
     */
    List<HoursEntry> findByUser(int userId);

    /**
     * Update entry hours in the hours logged table
     * @param entry the entry, with its new status and note
     * @return true if update was successful
     */
    boolean updateReview(HoursEntry entry);
}
