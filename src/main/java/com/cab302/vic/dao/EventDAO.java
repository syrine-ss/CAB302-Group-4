package com.cab302.vic.dao;

import com.cab302.vic.model.Event;

import java.util.List;
import java.util.Optional;

/**
 * DAO contract for events. Uses the same interface pattern as {@link UserDAO}
 * for consistency and testability.
 */
public interface EventDAO {

    /**
     * Create a new event into by inserting into the event database
     * @param event the event to be inserted into the db
     * @return the event parameter
     */
    Event create(Event event);

    /**
     * Find an event in the database by id
     * @param id an event id
     * @return a list including the event, empty if none found
     */
    Optional<Event> findById(int id);

    /**
     * Find all events in the database
     * @return a list of events
     */
    List<Event> findAll();

    /**
     * Find events in the database by a coordinator id
     * @param coordinatorId the coordinator id
     * @return list of events from coordinator id
     */
    List<Event> findByCoordinator(int coordinatorId);

    /**
     * Updates an existing event in the database
     * @param event event to be updated
     * @return boolean true if event was updated
     */
    boolean update(Event event);

    /**
     * Deletes an event in the database using its event id
     * @param id the id of event to be deleted
     * @return boolean true if updated
     */
    boolean delete(int id);
}
