package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeHoursDAO;
import com.cab302.vic.dao.FakeSignupDAO;
import com.cab302.vic.dao.FakeUserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ServiceFactory}, the Factory introduced to stop
 * controllers building their own SQLite DAOs.
 *
 * <p>The most important thing these prove is that the whole object graph
 * can be swapped for in-memory fakes. That was impossible before the
 * refactor, because each controller hard-coded {@code new SqliteUserDAO(...)}
 * in a field initialiser.
 */
class ServiceFactoryTest {

    @AfterEach
    void tearDown() {
        // Leave no shared state behind for the next test class.
        ServiceFactory.reset();
    }

    private ServiceFactory fakeBacked() {
        FakeEventDAO events = new FakeEventDAO();
        return new ServiceFactory(
                new FakeUserDAO(),
                events,
                new FakeSignupDAO(),
                new FakeHoursDAO(events));
    }

    @Test
    void exposesEveryService() {
        ServiceFactory factory = fakeBacked();

        assertNotNull(factory.auth());
        assertNotNull(factory.events());
        assertNotNull(factory.signups());
        assertNotNull(factory.hours());
    }

    @Test
    void returnsTheSameServiceInstanceEachTime() {
        ServiceFactory factory = fakeBacked();

        // Controllers call these on every screen load, so a new object graph
        // each time would be wasteful and could desynchronise shared state.
        assertSame(factory.auth(), factory.auth());
        assertSame(factory.events(), factory.events());
        assertSame(factory.signups(), factory.signups());
        assertSame(factory.hours(), factory.hours());
    }

    @Test
    void sharedInstanceIsASingleton() {
        ServiceFactory.reset();
        ServiceFactory.setInstance(fakeBacked());

        assertSame(ServiceFactory.getInstance(), ServiceFactory.getInstance());
    }

    @Test
    void theWholeGraphCanBeReplacedWithFakes() throws Exception {
        ServiceFactory fakes = fakeBacked();
        ServiceFactory.setInstance(fakes);

        assertSame(fakes, ServiceFactory.getInstance());

        // Prove it really is the fake graph: registering through the shared
        // factory writes to the in-memory DAO and never touches a database.
        var user = ServiceFactory.getInstance().auth()
                .register("syrine", "Strong!Pass1", "Syrine Shraim",
                        "s@example.com", com.cab302.vic.model.User.Role.COORDINATOR);

        assertTrue(user.getId() > 0);
    }

    @Test
    void resetClearsTheSharedInstance() {
        ServiceFactory fakes = fakeBacked();
        ServiceFactory.setInstance(fakes);
        assertSame(fakes, ServiceFactory.getInstance());

        ServiceFactory.reset();

        // A fresh instance is built on demand, so it is no longer our fake.
        assertNotSame(fakes, ServiceFactory.getInstance());
    }
}
