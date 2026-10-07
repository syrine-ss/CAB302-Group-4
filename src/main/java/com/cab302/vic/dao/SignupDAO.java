package com.cab302.vic.dao;

import com.cab302.vic.model.Signup;

import java.util.List;
import java.util.Optional;

/**
 * DAO contract for event signups. A volunteer has at most one signup per
 * event, so most lookups use the event and volunteer ids together.
 */
public interface SignupDAO {

    /**
     * Saves a new signup and assigns its id.
     *
     * @param signup the signup to save
     * @return the same signup, with its id set
     */
    Signup create(Signup signup);

    /**
     * @param eventId the event
     * @param userId  the volunteer
     * @return the volunteer's signup for that event, if there is one
     */
    Optional<Signup> find(int eventId, int userId);

    /**
     * @param eventId the event
     * @return every signup for the event, in the order people signed up
     */
    List<Signup> findByEvent(int eventId);

    /**
     * @param userId the volunteer
     * @return every event signup the volunteer has
     */
    List<Signup> findByUser(int userId);

    /**
     * @param eventId the event
     * @return how many volunteers are signed up for it
     */
    int countForEvent(int eventId);

    /**
     * Records whether a volunteer attended.
     *
     * @param eventId  the event
     * @param userId   the volunteer
     * @param attended true if they attended
     * @return true when a signup was found and updated
     */
    boolean setAttended(int eventId, int userId, boolean attended);

    /**
     * Removes a volunteer's signup.
     *
     * @param eventId the event
     * @param userId  the volunteer
     * @return true when a signup was found and removed
     */
    boolean delete(int eventId, int userId);
}
