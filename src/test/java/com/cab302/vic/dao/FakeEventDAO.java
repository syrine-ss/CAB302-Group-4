package com.cab302.vic.dao;

import com.cab302.vic.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** In-memory fake for {@link EventDAO}, used by service tests. */
public class FakeEventDAO implements EventDAO {

    private final Map<Integer, Event> byId = new HashMap<>();
    private int nextId = 1;

    @Override
    public Event create(Event event) {
        event.setId(nextId++);
        byId.put(event.getId(), event);
        return event;
    }

    @Override
    public Optional<Event> findById(int id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public List<Event> findByCoordinator(int coordinatorId) {
        return byId.values().stream()
                .filter(e -> e.getCreatedBy() == coordinatorId)
                .toList();
    }

    @Override
    public boolean update(Event event) {
        if (!byId.containsKey(event.getId())) return false;
        byId.put(event.getId(), event);
        return true;
    }

    @Override
    public boolean delete(int id) {
        return byId.remove(id) != null;
    }

    public int size() {
        return byId.size();
    }
}
