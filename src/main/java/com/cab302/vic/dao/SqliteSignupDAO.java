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

    public SqliteSignupDAO(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Signup create(Signup signup) {
        String sql = "INSERT INTO signups (event_id, user_id, signed_up_on, attended) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, signup.getEventId());
            ps.setInt(2, signup.getUserId());
            ps.setString(3, signup.getSignedUpOn());
            ps.setInt(4, signup.isAttended() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) signup.setId(keys.getInt(1));
            }
            return signup;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert signup for event " + signup.getEventId(), e);
        }
    }

    @Override
    public List<Signup> findByEvent(int eventId) {
        return list("SELECT * FROM signups WHERE event_id = ? ORDER BY id", ps -> ps.setInt(1, eventId));
    }

    @Override
    public List<Signup> findByUser(int userId) {
        return list("SELECT * FROM signups WHERE user_id = ? ORDER BY id", ps -> ps.setInt(1, userId));
    }

    @Override
    public Optional<Signup> find(int eventId, int userId) {
        String sql = "SELECT * FROM signups WHERE event_id = ? AND user_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up signup", e);
        }
    }

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
            throw new RuntimeException("Failed to count signups for event " + eventId, e);
        }
    }

    @Override
    public boolean update(Signup signup) {
        String sql = "UPDATE signups SET attended = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, signup.isAttended() ? 1 : 0);
            ps.setInt(2, signup.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update signup " + signup.getId(), e);
        }
    }

    @Override
    public boolean delete(int eventId, int userId) {
        String sql = "DELETE FROM signups WHERE event_id = ? AND user_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete signup", e);
        }
    }

    // --- helpers ---

    private List<Signup> list(String sql, SqlBinder binder) {
        List<Signup> out = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query signups", e);
        }
    }

    private static Signup mapRow(ResultSet rs) throws SQLException {
        return new Signup(
                rs.getInt("id"),
                rs.getInt("event_id"),
                rs.getInt("user_id"),
                rs.getString("signed_up_on"),
                rs.getInt("attended") == 1
        );
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
