package com.cab302.vic.dao;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory stand-in for {@link HoursDAO}.
 *
 * <p>Two of the real queries join against the events table to scope results
 * to one coordinator. This fake is given the {@link FakeEventDAO} so it can
 * reproduce that scoping, otherwise tests would pass here while the real
 * SQL filtered differently.
 */
public class FakeHoursDAO implements HoursDAO {

    private final Map<Integer, HoursEntry> byId = new LinkedHashMap<>();
    private final FakeEventDAO events;
    private int nextId = 1;

    public FakeHoursDAO(FakeEventDAO events) {
        this.events = events;
    }

    @Override
    public HoursEntry create(HoursEntry entry) {
        entry.setId(nextId++);
        byId.put(entry.getId(), entry);
        return entry;
    }

    @Override
    public Optional<HoursEntry> findById(int id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<HoursEntry> findByEvent(int eventId) {
        List<HoursEntry> out = new ArrayList<>();
        for (HoursEntry h : byId.values()) {
            if (h.getEventId() == eventId) out.add(h);
        }
        return out;
    }

    @Override
    public List<HoursEntry> findByUser(int userId) {
        List<HoursEntry> out = new ArrayList<>();
        for (HoursEntry h : byId.values()) {
            if (h.getUserId() == userId) out.add(h);
        }
        return out;
    }

    @Override
    public Optional<HoursEntry> find(int userId, int eventId) {
        return byId.values().stream()
                .filter(h -> h.getUserId() == userId && h.getEventId() == eventId)
                .findFirst();
    }

    @Override
    public List<HoursEntry> findPendingForCoordinator(int coordinatorId) {
        List<HoursEntry> out = new ArrayList<>();
        for (HoursEntry h : byId.values()) {
            if (h.isPending() && ownedBy(h, coordinatorId)) out.add(h);
        }
        return out;
    }

    @Override
    public double totalApprovedHoursForCoordinator(int coordinatorId) {
        double total = 0;
        for (HoursEntry h : byId.values()) {
            if (h.countsTowardsTotals() && ownedBy(h, coordinatorId)) {
                total += h.getHours();
            }
        }
        return total;
    }

    @Override
    public boolean update(HoursEntry entry) {
        if (!byId.containsKey(entry.getId())) return false;
        byId.put(entry.getId(), entry);
        return true;
    }

    private boolean ownedBy(HoursEntry entry, int coordinatorId) {
        return events.findById(entry.getEventId())
                .map(Event::getCreatedBy)
                .orElse(-1) == coordinatorId;
    }

    public int size() {
        return byId.size();
    }
}
