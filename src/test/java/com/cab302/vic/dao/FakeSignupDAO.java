package com.cab302.vic.dao;

import com.cab302.vic.model.Signup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** In-memory fake for {@link SignupDAO}, used by service tests. */
public class FakeSignupDAO implements SignupDAO {

    private final List<Signup> signups = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Signup create(Signup signup) {
        signup.setId(nextId++);
        signups.add(signup);
        return signup;
    }

    @Override
    public Optional<Signup> find(int eventId, int userId) {
        return signups.stream()
                .filter(s -> s.getEventId() == eventId && s.getUserId() == userId)
                .findFirst();
    }

    @Override
    public List<Signup> findByEvent(int eventId) {
        return signups.stream()
                .filter(s -> s.getEventId() == eventId)
                .sorted(Comparator.comparingInt(Signup::getId))
                .toList();
    }

    @Override
    public List<Signup> findByUser(int userId) {
        return signups.stream()
                .filter(s -> s.getUserId() == userId)
                .sorted(Comparator.comparingInt(Signup::getId))
                .toList();
    }

    @Override
    public int countForEvent(int eventId) {
        return (int) signups.stream().filter(s -> s.getEventId() == eventId).count();
    }

    @Override
    public boolean setAttended(int eventId, int userId, boolean attended) {
        Optional<Signup> signup = find(eventId, userId);
        signup.ifPresent(s -> s.setAttended(attended));
        return signup.isPresent();
    }

    @Override
    public boolean delete(int eventId, int userId) {
        return signups.removeIf(s -> s.getEventId() == eventId && s.getUserId() == userId);
    }

    public int size() {
        return signups.size();
    }
}
