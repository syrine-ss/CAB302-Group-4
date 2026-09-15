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

    /** Insert a new user and return the same user with its assigned id. */
    User create(User user);

    /** Find a user by primary key. Empty when no user exists with that id. */
    Optional<User> findById(int id);

    /** Find a user by username. Usernames are unique. */
    Optional<User> findByUsername(String username);

    /** True when a user with the given username already exists. */
    boolean existsByUsername(String username);

    /** Return every user. Primarily used for admin views and tests. */
    List<User> findAll();
}
