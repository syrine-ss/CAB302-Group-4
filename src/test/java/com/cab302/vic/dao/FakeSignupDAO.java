package com.cab302.vic.dao;

import com.cab302.vic.model.Signup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory stand-in for {@link SignupDAO}.
 *
 * <p>Used instead of a mocking framework so the behaviour of the double is
 * plain to read, and so tests run without a database.
 */
public class FakeSignupDAO implements SignupDAO {

    private final Map<Integer, Signup> byId = new LinkedHashMap<>();
    private int nextId = 1;

    @Override
    public Signup create(Signup signup) {
        signup.setId(nextId++);
        byId.put(signup.getId(), signup);
        return signup;
    }

    @Override
    public List<Signup> findByEvent(int eventId) {
        List<Signup> out = new ArrayList<>();
        for (Signup s : byId.values()) {
            if (s.getEventId() == eventId) out.add(s);
        }
        return out;
    }

    @Override
    public List<Signup> findByUser(int userId) {
        List<Signup> out = new ArrayList<>();
        for (Signup s : byId.values()) {
            if (s.getUserId() == userId) out.add(s);
        }
        return out;
    }

    @Override
    public Optional<Signup> find(int eventId, int userId) {
        return byId.values().stream()
                .filter(s -> s.getEventId() == eventId && s.getUserId() == userId)
                .findFirst();
    }

    @Override
    public int countForEvent(int eventId) {
        return findByEvent(eventId).size();
    }

    @Override
    public boolean update(Signup signup) {
        if (!byId.containsKey(signup.getId())) return false;
        byId.put(signup.getId(), signup);
        return true;
    }

    @Override
    public boolean delete(int eventId, int userId) {
        Optional<Signup> match = find(eventId, userId);
        match.ifPresent(s -> byId.remove(s.getId()));
        return match.isPresent();
    }

    /** Total rows held, for assertions about persistence. */
    public int size() {
        return byId.size();
    }
}
