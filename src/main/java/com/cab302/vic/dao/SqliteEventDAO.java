package com.cab302.vic.dao;

import com.cab302.vic.model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** SQLite-backed implementation of {@link EventDAO}. */
public class SqliteEventDAO implements EventDAO {

    private final DatabaseManager db;

    public SqliteEventDAO(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Event create(Event event) {
        String sql = "INSERT INTO events (title, description, event_date, event_time, " +
                "location, volunteers_needed, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindEvent(ps, event);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) event.setId(keys.getInt(1));
            }
            return event;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert event: " + event.getTitle(), e);
        }
    }

    @Override
    public Optional<Event> findById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find event id=" + id, e);
        }
    }

    @Override
    public List<Event> findAll() {
        return list("SELECT * FROM events ORDER BY event_date ASC", ps -> {});
    }

    @Override
    public List<Event> findByCoordinator(int coordinatorId) {
        return list("SELECT * FROM events WHERE created_by = ? ORDER BY event_date ASC",
                ps -> ps.setInt(1, coordinatorId));
    }

    @Override
    public boolean update(Event event) {
        String sql = "UPDATE events SET title=?, description=?, event_date=?, event_time=?, " +
                "location=?, volunteers_needed=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getEventDate());
            ps.setString(4, event.getEventTime());
            ps.setString(5, event.getLocation());
            ps.setInt(6, event.getVolunteersNeeded());
            ps.setInt(7, event.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update event id=" + event.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM events WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete event id=" + id, e);
        }
    }

    // --- helpers ---

    private List<Event> list(String sql, SqlBinder binder) {
        List<Event> events = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) events.add(mapRow(rs));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query events", e);
        }
    }

    private static void bindEvent(PreparedStatement ps, Event e) throws SQLException {
        ps.setString(1, e.getTitle());
        ps.setString(2, e.getDescription());
        ps.setString(3, e.getEventDate());
        ps.setString(4, e.getEventTime());
        ps.setString(5, e.getLocation());
        ps.setInt(6, e.getVolunteersNeeded());
        ps.setInt(7, e.getCreatedBy());
    }

    private static Event mapRow(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("event_date"),
                rs.getString("event_time"),
                rs.getString("location"),
                rs.getInt("volunteers_needed"),
                rs.getInt("created_by")
        );
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
