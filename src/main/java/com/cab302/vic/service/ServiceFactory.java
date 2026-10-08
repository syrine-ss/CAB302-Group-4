package com.cab302.vic.service;

import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.dao.SqliteEventDAO;
import com.cab302.vic.dao.SqliteUserDAO;
import com.cab302.vic.dao.UserDAO;

/**
 * Builds the application's services once and hands the same instances to every
 * controller (Factory pattern, held as a Singleton).
 *
 * <p>Before this class, each controller wired up its own service and DAO, for
 * example {@code new EventService(new SqliteEventDAO(DatabaseManager.getInstance()))}.
 * That repeated the same construction in five places and tied every controller
 * to SQLite. Controllers now ask the factory for a service and never name a DAO,
 * so the choice of storage is made in exactly one place.
 *
 * <p>Tests, or a different storage back end, can build a factory from their own
 * DAOs with {@link #ServiceFactory(UserDAO, EventDAO)}.
 */
public final class ServiceFactory {

    private static ServiceFactory instance;

    private final AuthService authService;
    private final EventService eventService;

    /**
     * Builds a factory whose services use the given DAOs.
     *
     * @param userDAO  storage for users
     * @param eventDAO storage for events
     * @throws IllegalArgumentException if either DAO is null
     */
    public ServiceFactory(UserDAO userDAO, EventDAO eventDAO) {
        if (userDAO == null || eventDAO == null) {
            throw new IllegalArgumentException("DAOs must not be null");
        }
        this.authService = new AuthService(userDAO);
        this.eventService = new EventService(eventDAO);
    }

    /**
     * Builds a factory backed by SQLite through the given database.
     *
     * @param db the database the services should read and write
     * @return a factory using SQLite DAOs
     */
    public static ServiceFactory forDatabase(DatabaseManager db) {
        return new ServiceFactory(new SqliteUserDAO(db), new SqliteEventDAO(db));
    }

    /**
     * The shared factory the application uses, backed by the default database.
     * Created on first use.
     *
     * @return the application-wide factory
     */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = forDatabase(DatabaseManager.getInstance());
        }
        return instance;
    }

    /** @return the service for registering and logging in users */
    public AuthService auth() {
        return authService;
    }

    /** @return the service for creating, editing and listing events */
    public EventService events() {
        return eventService;
    }
}
