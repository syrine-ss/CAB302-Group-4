package com.cab302.vic.dao;

import com.cab302.vic.model.HoursEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** In-memory fake for {@link HoursDAO}, used by service tests. */
public class FakeHoursDAO implements HoursDAO {

    private final List<HoursEntry> entries = new ArrayList<>();
    private int nextId = 1;

    @Override
    public HoursEntry create(HoursEntry entry) {
        entry.setId(nextId++);
        entries.add(entry);
        return entry;
    }

    @Override
    public Optional<HoursEntry> findById(int id) {
        return entries.stream().filter(e -> e.getId() == id).findFirst();
    }

    @Override
    public List<HoursEntry> findByEvent(int eventId) {
        return entries.stream()
                .filter(e -> e.getEventId() == eventId)
                .sorted(Comparator.comparingInt(HoursEntry::getId))
                .toList();
    }

    @Override
    public List<HoursEntry> findByUser(int userId) {
        return entries.stream()
                .filter(e -> e.getUserId() == userId)
                .sorted(Comparator.comparingInt(HoursEntry::getId))
                .toList();
    }

    @Override
    public boolean updateReview(HoursEntry entry) {
        return findById(entry.getId()).isPresent();
    }

    public int size() {
        return entries.size();
    }
}
