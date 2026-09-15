package com.cab302.vic.dao;

import com.cab302.vic.model.Event;

import java.util.List;
import java.util.Optional;

/**
 * DAO contract for events. Uses the same interface pattern as {@link UserDAO}
 * for consistency and testability.
 */
public interface EventDAO {

    Event create(Event event);

    Optional<Event> findById(int id);

    List<Event> findAll();

    List<Event> findByCoordinator(int coordinatorId);

    /** @return true when the row was updated. */
    boolean update(Event event);

    /** @return true when the row was deleted. */
    boolean delete(int id);
}
