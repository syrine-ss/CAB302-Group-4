package com.cab302.vic.dao;

import com.cab302.vic.model.Signup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** SQLite-backed implementation of {@link SignupDAO}. */
public class SqliteSignupDAO implements SignupDAO {

    private final DatabaseManager db;

    /**
     * Creates a signup DAO using the given database
     * @param db the database manager
     */
    public SqliteSignupDAO(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Insert a sign-up for an event into the database
     * @param signup the event signup
     * @return the signup
     * @throws RuntimeException if failed to save signup for event
     */
    @Override
    public Signup create(Signup signup) {
        String sql = "INSERT INTO signups (event_id, user_id, attended, signed_up_on) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, signup.getEventId());
            ps.setInt(2, signup.getUserId());
            ps.setInt(3, signup.isAttended() ? 1 : 0);
            ps.setString(4, signup.getSignedUpOn());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) signup.setId(keys.getInt(1));
            }
            return signup;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save signup for event id=" + signup.getEventId(), e);
        }
    }

    /**
     * Find signups using for an event and user, using an event id and user id
     * @param eventId the event
     * @param userId  the volunteer
     * @return the list of signups, empty if none found
     */
    @Override
    public Optional<Signup> find(int eventId, int userId) {
        List<Signup> found = list("SELECT * FROM signups WHERE event_id = ? AND user_id = ?",
                ps -> {
                    ps.setInt(1, eventId);
                    ps.setInt(2, userId);
                });
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    /**
     * Find signups for a specific event, using the event id
     * @param eventId the event
     * @return the signups to event if found
     */
    @Override
    public List<Signup> findByEvent(int eventId) {
        return list("SELECT * FROM signups WHERE event_id = ? ORDER BY id ASC",
                ps -> ps.setInt(1, eventId));
    }

    /**
     * Find signups for a specific event, using the event id
     * @param userId the volunteer
     * @return the signups to event if found
     */
    @Override
    public List<Signup> findByUser(int userId) {
        return list("SELECT * FROM signups WHERE user_id = ? ORDER BY id ASC",
                ps -> ps.setInt(1, userId));
    }

    /**
     * Count the number of signups for an event
     * @param eventId the event
     * @return the number of signups
     * @throws RuntimeException is failed to count signups for event
     */
    @Override
    public int countForEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM signups WHERE event_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count signups for event id=" + eventId, e);
        }
    }

    /**
     * Set a volunteer as attended the event
     * @param eventId  the event
     * @param userId   the volunteer
     * @param attended true if they attended
     * @return true if set to attended successful
     */
    @Override
    public boolean setAttended(int eventId, int userId, boolean attended) {
        return update("UPDATE signups SET attended = ? WHERE event_id = ? AND user_id = ?",
                ps -> {
                    ps.setInt(1, attended ? 1 : 0);
                    ps.setInt(2, eventId);
                    ps.setInt(3, userId);
                });
    }

    /**
     * Delete entry from signups table using event id and volunteer id
     * @param eventId the event
     * @param userId  the volunteer
     * @return true if delete was successful
     */
    @Override
    public boolean delete(int eventId, int userId) {
        return update("DELETE FROM signups WHERE event_id = ? AND user_id = ?",
                ps -> {
                    ps.setInt(1, eventId);
                    ps.setInt(2, userId);
                });
    }

    // --- helpers ---

    private List<Signup> list(String sql, SqlBinder binder) {
        List<Signup> signups = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) signups.add(mapRow(rs));
            }
            return signups;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query signups", e);
        }
    }

    private boolean update(String sql, SqlBinder binder) {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update signups", e);
        }
    }

    private static Signup mapRow(ResultSet rs) throws SQLException {
        return new Signup(
                rs.getInt("id"),
                rs.getInt("event_id"),
                rs.getInt("user_id"),
                rs.getInt("attended") == 1,
                rs.getString("signed_up_on")
        );
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
