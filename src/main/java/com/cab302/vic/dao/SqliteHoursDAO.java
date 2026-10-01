package com.cab302.vic.dao;

import com.cab302.vic.model.HoursEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** SQLite-backed implementation of {@link HoursDAO}. */
public class SqliteHoursDAO implements HoursDAO {

    private final DatabaseManager db;

    public SqliteHoursDAO(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public HoursEntry create(HoursEntry entry) {
        String sql = "INSERT INTO hours_logged (user_id, event_id, hours, status, logged_on, review_note) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getUserId());
            ps.setInt(2, entry.getEventId());
            ps.setDouble(3, entry.getHours());
            ps.setString(4, entry.getStatus().name());
            ps.setString(5, entry.getLoggedOn());
            ps.setString(6, entry.getReviewNote());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entry.setId(keys.getInt(1));
            }
            return entry;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert hours entry", e);
        }
    }

    @Override
    public Optional<HoursEntry> findById(int id) {
        return single("SELECT * FROM hours_logged WHERE id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<HoursEntry> findByEvent(int eventId) {
        return list("SELECT * FROM hours_logged WHERE event_id = ? ORDER BY id",
                ps -> ps.setInt(1, eventId));
    }

    @Override
    public List<HoursEntry> findByUser(int userId) {
        return list("SELECT * FROM hours_logged WHERE user_id = ? ORDER BY id",
                ps -> ps.setInt(1, userId));
    }

    @Override
    public Optional<HoursEntry> find(int userId, int eventId) {
        return single("SELECT * FROM hours_logged WHERE user_id = ? AND event_id = ?", ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
        });
    }

    @Override
    public List<HoursEntry> findPendingForCoordinator(int coordinatorId) {
        // Joined against events so a coordinator only ever reviews claims on
        // events they created, never someone else's.
        String sql = "SELECT h.* FROM hours_logged h "
                + "JOIN events e ON h.event_id = e.id "
                + "WHERE e.created_by = ? AND h.status = 'PENDING' "
                + "ORDER BY h.id";
        return list(sql, ps -> ps.setInt(1, coordinatorId));
    }

    @Override
    public double totalApprovedHoursForCoordinator(int coordinatorId) {
        String sql = "SELECT COALESCE(SUM(h.hours), 0) FROM hours_logged h "
                + "JOIN events e ON h.event_id = e.id "
                + "WHERE e.created_by = ? AND h.status = 'APPROVED'";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coordinatorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to total approved hours", e);
        }
    }

    @Override
    public boolean update(HoursEntry entry) {
        String sql = "UPDATE hours_logged SET hours = ?, status = ?, review_note = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, entry.getHours());
            ps.setString(2, entry.getStatus().name());
            ps.setString(3, entry.getReviewNote());
            ps.setInt(4, entry.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update hours entry " + entry.getId(), e);
        }
    }

    // --- helpers ---

    private Optional<HoursEntry> single(String sql, SqlBinder binder) {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query hours entry", e);
        }
    }

    private List<HoursEntry> list(String sql, SqlBinder binder) {
        List<HoursEntry> out = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query hours entries", e);
        }
    }

    private static HoursEntry mapRow(ResultSet rs) throws SQLException {
        String note = rs.getString("review_note");
        return new HoursEntry(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("event_id"),
                rs.getDouble("hours"),
                HoursEntry.Status.valueOf(rs.getString("status")),
                rs.getString("logged_on"),
                note == null ? "" : note
        );
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
