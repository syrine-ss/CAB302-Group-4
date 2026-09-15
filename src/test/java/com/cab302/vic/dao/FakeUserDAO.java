package com.cab302.vic.dao;

import com.cab302.vic.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory fake implementation of {@link UserDAO} for tests.
 * Used instead of a mocking framework so tests stay dependency-free and
 * the behaviour of the fake is easy to inspect.
 */
public class FakeUserDAO implements UserDAO {

    private final Map<Integer, User> byId = new HashMap<>();
    private final Map<String, User> byUsername = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        user.setId(nextId++);
        byId.put(user.getId(), user);
        byUsername.put(user.getUsername(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return byUsername.containsKey(username);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }

    /** Convenience: number of users currently in the fake. */
    public int size() {
        return byId.size();
    }
}
