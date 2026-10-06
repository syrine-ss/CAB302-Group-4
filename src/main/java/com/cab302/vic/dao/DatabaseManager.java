package com.cab302.vic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the SQLite database connection and schema initialisation.
 *
 * Refactored from a hard-coded singleton so that tests can construct an
 * in-memory or per-test database. The application still uses the default
 * shared instance via {@link #getInstance()}.
 */
public class DatabaseManager {

    private static final String DEFAULT_URL = "jdbc:sqlite:vic.db";
    private static DatabaseManager instance;

    private final String url;

    /**
     * Construct a manager backed by the given JDBC URL. Useful for tests.
     * @param url The JDBC url
     * */
    public DatabaseManager(String url) {
        this.url = url;
    }

    /**
     * Shared instance backed by the default file (vic.db).
     * @return the database manager instance
     * */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(DEFAULT_URL);
        }
        return instance;
    }

    /**
     * Open a new connection. Callers must close it (or use try-with-resources).
     * @return a database connection
     * @throws SQLException if a database connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Create tables if they don't already exist. Safe to call every startup.
     * @throws RuntimeException if the database cannot be initialised
     */
    public void initialise() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                full_name TEXT NOT NULL,
                email TEXT,
                role TEXT NOT NULL
            );
            """;

        String createEvents = """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                event_date TEXT NOT NULL,
                event_time TEXT,
                location TEXT,
                volunteers_needed INTEGER,
                created_by INTEGER,
                FOREIGN KEY (created_by) REFERENCES users(id)
            );
            """;

        String createSignups = """
            CREATE TABLE IF NOT EXISTS signups (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                signed_up_on TEXT,
                attended INTEGER DEFAULT 0,
                UNIQUE (event_id, user_id),
                FOREIGN KEY (event_id) REFERENCES events(id),
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
            """;

        String createHours = """
            CREATE TABLE IF NOT EXISTS hours_logged (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                hours REAL NOT NULL,
                status TEXT NOT NULL DEFAULT 'PENDING',
                logged_on TEXT NOT NULL,
                review_note TEXT DEFAULT '',
                UNIQUE (user_id, event_id),
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (event_id) REFERENCES events(id)
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createEvents);
            stmt.execute(createSignups);
            stmt.execute(createHours);
            migrate(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialise database", e);
        }
    }

    /**
     * Bring an older database file up to the current schema.
     */
    private void migrate(Connection conn) throws SQLException {
        addColumnIfMissing(conn, "signups", "signed_up_on", "TEXT");
        addColumnIfMissing(conn, "hours_logged", "status", "TEXT NOT NULL DEFAULT 'PENDING'");
        addColumnIfMissing(conn, "hours_logged", "review_note", "TEXT DEFAULT ''");
    }

    private void addColumnIfMissing(Connection conn, String table, String column, String type)
            throws SQLException {
        if (columnExists(conn, table, column)) {
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        }
    }

    private boolean columnExists(Connection conn, String table, String column) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }
}
