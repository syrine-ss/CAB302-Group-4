package com.cab302.vic.dao;

import com.cab302.vic.model.Signup;

import java.util.List;
import java.util.Optional;

/**
 * DAO contract for event signups.
 *
 * <p>Declared as an interface for the same reasons as {@link UserDAO} and
 * {@link EventDAO}: services depend on the abstraction, so they can be unit
 * tested against an in-memory fake, and the storage technology could change
 * without touching business logic.
 */
public interface SignupDAO {

    /** Persist a new signup and return it with its assigned id. */
    Signup create(Signup signup);

    /** Every signup for one event, oldest first. */
    List<Signup> findByEvent(int eventId);

    /** Every signup belonging to one volunteer. */
    List<Signup> findByUser(int userId);

    /** The signup pairing this volunteer with this event, if one exists. */
    Optional<Signup> find(int eventId, int userId);

    /** How many volunteers are currently signed up to an event. */
    int countForEvent(int eventId);

    /** Persist a change to an existing signup, e.g. attendance. @return true when a row changed. */
    boolean update(Signup signup);

    /** Remove a signup entirely, used when a volunteer withdraws. @return true when a row was removed. */
    boolean delete(int eventId, int userId);
}
