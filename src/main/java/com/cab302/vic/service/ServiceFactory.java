package com.cab302.vic.service;

import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.dao.EventDAO;
import com.cab302.vic.dao.HoursDAO;
import com.cab302.vic.dao.SignupDAO;
import com.cab302.vic.dao.SqliteEventDAO;
import com.cab302.vic.dao.SqliteHoursDAO;
import com.cab302.vic.dao.SqliteSignupDAO;
import com.cab302.vic.dao.SqliteUserDAO;
import com.cab302.vic.dao.UserDAO;

/**
 * Single place where services are wired to their data access objects.
 *
 * <p><b>Why this exists.</b> Every controller used to build its own
 * dependencies, for example:
 *
 * <pre>{@code
 * private final AuthService authService =
 *         new AuthService(new SqliteUserDAO(DatabaseManager.getInstance()));
 * }</pre>
 *
 * That repeated the same wiring in five controllers and, worse, made the
 * controller layer depend directly on the SQLite classes. The DAO interfaces
 * existed precisely so the rest of the app would not know which database was
 * behind them, and constructing {@code SqliteUserDAO} in a controller threw
 * that away. Our own class diagram flagged the dependency as a problem.
 *
 * <p>Controllers now ask this factory instead:
 *
 * <pre>{@code
 * private final AuthService authService = ServiceFactory.getInstance().auth();
 * }</pre>
 *
 * <p>The wiring lives in one file, controllers no longer name a database
 * technology, and tests can swap the whole graph through
 * {@link #setInstance(ServiceFactory)}.
 */
public class ServiceFactory {

    private static ServiceFactory instance;

    private final AuthService authService;
    private final EventService eventService;
    private final SignupService signupService;
    private final HoursService hoursService;

    /**
     * Build a factory over the given DAOs. Public so tests can assemble a
     * factory backed by in-memory fakes and hand it to {@link #setInstance}.
     */
    public ServiceFactory(UserDAO userDAO, EventDAO eventDAO,
                          SignupDAO signupDAO, HoursDAO hoursDAO) {
        this.authService = new AuthService(userDAO);
        this.eventService = new EventService(eventDAO);
        this.signupService = new SignupService(signupDAO, eventDAO);
        this.hoursService = new HoursService(hoursDAO, signupDAO, eventDAO);
    }

    /** The application's shared factory, backed by SQLite on first use. */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = createSqliteBacked(DatabaseManager.getInstance());
        }
        return instance;
    }

    /**
     * Replace the shared factory. Intended for tests, which pass a factory
     * built over fakes so no test touches a real database.
     */
    public static synchronized void setInstance(ServiceFactory replacement) {
        instance = replacement;
    }

    /** Drop the shared factory so the next call rebuilds it. */
    public static synchronized void reset() {
        instance = null;
    }

    /** Wire the full SQLite-backed object graph for a given database. */
    public static ServiceFactory createSqliteBacked(DatabaseManager db) {
        return new ServiceFactory(
                new SqliteUserDAO(db),
                new SqliteEventDAO(db),
                new SqliteSignupDAO(db),
                new SqliteHoursDAO(db));
    }

    public AuthService auth() { return authService; }
    public EventService events() { return eventService; }
    public SignupService signups() { return signupService; }
    public HoursService hours() { return hoursService; }
}
