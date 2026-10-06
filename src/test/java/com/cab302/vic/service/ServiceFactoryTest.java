package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeUserDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ServiceFactory}, using fake DAOs so no database is touched.
 */
class ServiceFactoryTest {

    private FakeUserDAO userDAO;
    private FakeEventDAO eventDAO;
    private ServiceFactory factory;

    @BeforeEach
    void setUp() {
        userDAO = new FakeUserDAO();
        eventDAO = new FakeEventDAO();
        factory = new ServiceFactory(userDAO, eventDAO);
    }

    @Test
    void returnsTheSameServiceEachTime() {
        assertSame(factory.auth(), factory.auth());
        assertSame(factory.events(), factory.events());
    }

    @Test
    void authServiceUsesTheSuppliedUserDao() throws AuthException {
        factory.auth().register("sam", "Str0ng!pass", "Sam Lee", "sam@example.com",
                User.Role.VOLUNTEER);

        assertTrue(userDAO.findByUsername("sam").isPresent());
    }

    @Test
    void eventServiceUsesTheSuppliedEventDao() throws EventException {
        Event created = factory.events().create("Tree planting", "",
                LocalDate.now().plusDays(7).toString(), "09:00", "Park", 8, 1);

        assertEquals(1, eventDAO.size());
        assertTrue(eventDAO.findById(created.getId()).isPresent());
    }

    @Test
    void rejectsMissingDaos() {
        assertThrows(IllegalArgumentException.class, () -> new ServiceFactory(null, eventDAO));
        assertThrows(IllegalArgumentException.class, () -> new ServiceFactory(userDAO, null));
    }

    @Test
    void sharedInstanceIsCreatedOnceAndReused() {
        assertSame(ServiceFactory.getInstance(), ServiceFactory.getInstance());
    }
}
