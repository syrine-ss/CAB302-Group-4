package com.cab302.vic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that manages the SQLite database connection and schema.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:vic.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    /**
     * Create tables if they do not already exist.
     * Called once on application startup.
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
                attended INTEGER DEFAULT 0,
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
                approved INTEGER DEFAULT 0,
                logged_on TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (event_id) REFERENCES events(id)
            );
            """;

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createEvents);
            stmt.execute(createSignups);
            stmt.execute(createHours);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialise database", e);
        }
    }
}
