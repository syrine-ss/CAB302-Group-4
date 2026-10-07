package com.cab302.vic.dao;

import com.cab302.vic.model.HoursEntry;

import java.util.List;
import java.util.Optional;

/** DAO contract for hours that volunteers log against events. */
public interface HoursDAO {

    /**
     * Saves a new entry and assigns its id.
     *
     * @param entry the entry to save
     * @return the same entry, with its id set
     */
    HoursEntry create(HoursEntry entry);

    /**
     * @param id the entry id
     * @return the entry, if it exists
     */
    Optional<HoursEntry> findById(int id);

    /**
     * @param eventId the event
     * @return every entry logged against the event, oldest first
     */
    List<HoursEntry> findByEvent(int eventId);

    /**
     * @param userId the volunteer
     * @return every entry the volunteer has logged, oldest first
     */
    List<HoursEntry> findByUser(int userId);

    /**
     * Saves a coordinator's decision on an entry.
     *
     * @param entry the entry, with its new status and note
     * @return true when the entry was found and updated
     */
    boolean updateReview(HoursEntry entry);
}
