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

    /**
     * Creates an hours DAO using the given database
     * @param db the database manager
     */
    public SqliteHoursDAO(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Insert hours logged by a volunteer into database
     * @param entry the entry hours logged by volunteer
     * @return the entry inputted
     */
    @Override
    public HoursEntry create(HoursEntry entry) {
        String sql = "INSERT INTO hours_logged (user_id, event_id, hours, status, review_note, logged_on) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getUserId());
            ps.setInt(2, entry.getEventId());
            ps.setDouble(3, entry.getHours());
            ps.setString(4, entry.getStatus().name());
            ps.setString(5, entry.getReviewNote());
            ps.setString(6, entry.getLoggedOn());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entry.setId(keys.getInt(1));
            }
            return entry;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save hours for event id=" + entry.getEventId(), e);
        }
    }

    /**
     * Find hours logged in database using its id
     * @param id the entry id
     * @return list hours logged by that id, empty if none found
     */
    @Override
    public Optional<HoursEntry> findById(int id) {
        List<HoursEntry> found = list("SELECT * FROM hours_logged WHERE id = ?", ps -> ps.setInt(1, id));
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    /**
     * Find hours logged in database using the relevant event id
     * @param eventId the event
     * @return list of hours logged for that event id if any found, oldest first
     */
    @Override
    public List<HoursEntry> findByEvent(int eventId) {
        return list("SELECT * FROM hours_logged WHERE event_id = ? ORDER BY id ASC",
                ps -> ps.setInt(1, eventId));
    }

    /**
     * Find hours logged in database using the relevant volunteer id
     * @param userId the volunteer
     * @return list of hours logged for that volunteer id if any found
     */
    @Override
    public List<HoursEntry> findByUser(int userId) {
        return list("SELECT * FROM hours_logged WHERE user_id = ? ORDER BY id ASC",
                ps -> ps.setInt(1, userId));
    }

    /**
     * Update entry hours in the hours logged table
     * @param entry the entry, with its new status and note
     * @return true if update was successful
     * @throws RuntimeException if failed to update hours
     */
    @Override
    public boolean updateReview(HoursEntry entry) {
        String sql = "UPDATE hours_logged SET status = ?, review_note = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entry.getStatus().name());
            ps.setString(2, entry.getReviewNote());
            ps.setInt(3, entry.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update hours id=" + entry.getId(), e);
        }
    }

    // --- helpers ---

    private List<HoursEntry> list(String sql, SqlBinder binder) {
        List<HoursEntry> entries = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) entries.add(mapRow(rs));
            }
            return entries;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query hours", e);
        }
    }

    private static HoursEntry mapRow(ResultSet rs) throws SQLException {
        return new HoursEntry(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("event_id"),
                rs.getDouble("hours"),
                HoursEntry.Status.fromDb(rs.getString("status")),
                rs.getString("review_note"),
                rs.getString("logged_on")
        );
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
