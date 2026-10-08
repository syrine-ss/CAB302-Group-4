package com.cab302.vic.dao;

import com.cab302.vic.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entities.
 * Uses an interface so services can be tested against a fake in-memory
 * implementation, and so the SQLite implementation could be swapped out
 * (e.g. for a different database) without changing service code.
 */
public interface UserDAO {

    /**
     * Insert a new user and return the same user with its assigned id
     * @param user the new user
     * @return the new user
     */
    User create(User user);

    /**
     * Find a user by primary key. Empty when no user exists with that id.
     * @param id the user id
     * @return the user, otherwise an empty list
     */
    Optional<User> findById(int id);

    /**
     * Find a user by username. Usernames are unique.
     * @param username username to be found
     * @return user found by username, empty list otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Find if username exists in database
     * @param username username to be checked
     * @return true if in username is in table
     */
    boolean existsByUsername(String username);

    /**
     * Get all users in database
     * @return list of all users
     */
    List<User> findAll();
}
