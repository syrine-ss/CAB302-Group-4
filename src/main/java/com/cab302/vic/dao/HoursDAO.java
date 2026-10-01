package com.cab302.vic.dao;

import com.cab302.vic.model.HoursEntry;

import java.util.List;
import java.util.Optional;

/** DAO contract for logged volunteer hours and their approval state. */
public interface HoursDAO {

    /** Persist a new hours claim and return it with its assigned id. */
    HoursEntry create(HoursEntry entry);

    Optional<HoursEntry> findById(int id);

    /** Every claim against one event, whatever its status. */
    List<HoursEntry> findByEvent(int eventId);

    /** Every claim made by one volunteer. */
    List<HoursEntry> findByUser(int userId);

    /** The claim this volunteer made against this event, if any. */
    Optional<HoursEntry> find(int userId, int eventId);

    /** Claims still awaiting a decision on events created by the given coordinator. */
    List<HoursEntry> findPendingForCoordinator(int coordinatorId);

    /**
     * Total approved hours across events created by the given coordinator.
     * Only approved hours are counted, so impact figures reflect verified data.
     */
    double totalApprovedHoursForCoordinator(int coordinatorId);

    /** @return true when a row changed. */
    boolean update(HoursEntry entry);
}
