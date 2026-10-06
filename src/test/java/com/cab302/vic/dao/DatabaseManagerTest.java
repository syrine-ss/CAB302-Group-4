package com.cab302.vic.dao;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks the schema against a real SQLite file, since the upgrade path for
 * older databases can only be tested on an actual database.
 */
class DatabaseManagerTest {

    private static DatabaseManager freshDatabase() throws IOException {
        Path file = Files.createTempFile("vic-test-", ".db");
        file.toFile().deleteOnExit();
        Files.delete(file);
        return new DatabaseManager("jdbc:sqlite:" + file);
    }

    private static List<String> columns(DatabaseManager db, String table) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }

    @Test
    void freshDatabaseHasSignupAndHoursColumns() throws Exception {
        DatabaseManager db = freshDatabase();
        db.initialise();

        assertTrue(columns(db, "signups").contains("signed_up_on"));
        assertTrue(columns(db, "hours_logged").contains("status"));
        assertTrue(columns(db, "hours_logged").contains("review_note"));
    }

    @Test
    void initialiseUpgradesDatabaseFromEarlierSprint() throws Exception {
        DatabaseManager db = freshDatabase();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE signups (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "event_id INTEGER NOT NULL, user_id INTEGER NOT NULL, attended INTEGER DEFAULT 0)");
            stmt.execute("CREATE TABLE hours_logged (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER NOT NULL, event_id INTEGER NOT NULL, hours REAL NOT NULL, "
                    + "approved INTEGER DEFAULT 0, logged_on TEXT NOT NULL)");
            stmt.execute("INSERT INTO hours_logged (user_id, event_id, hours, logged_on) "
                    + "VALUES (1, 1, 2.0, '2026-10-01')");
        }

        db.initialise();

        assertTrue(columns(db, "signups").contains("signed_up_on"));
        assertTrue(columns(db, "hours_logged").contains("status"));
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT status FROM hours_logged")) {
            assertTrue(rs.next());
            assertEquals("PENDING", rs.getString("status"));
        }
    }

    @Test
    void initialiseIsSafeToRunTwice() throws Exception {
        DatabaseManager db = freshDatabase();
        db.initialise();
        db.initialise();

        long statusColumns = columns(db, "hours_logged").stream()
                .filter("status"::equals)
                .count();
        assertEquals(1L, statusColumns);
    }
}
