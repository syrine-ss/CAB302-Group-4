package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour-focused tests for {@link EventService}.
 * Followed Red-Green-Refactor:
 * 1. Wrote the "no past dates" test first, watched it fail (Red).
 * 2. Added the LocalDate.isBefore check to make it pass (Green).
 * 3. Refactored the date parsing into a private helper (Refactor).
 */
class EventServiceTest {

    private FakeEventDAO eventDAO;
    private EventService service;
    private static final int COORDINATOR_ID = 42;

    @BeforeEach
    void setUp() {
        eventDAO = new FakeEventDAO();
        service = new EventService(eventDAO);
    }

    private static String futureDate() {
        return LocalDate.now().plusDays(14).toString();
    }

    private static String pastDate() {
        return LocalDate.now().minusDays(1).toString();
    }

    // ---------- create ----------

    @Test
    void createStoresEventAndAssignsId() throws EventException {
        Event event = service.create("Beach clean-up", "Bring gloves",
                futureDate(), "09:00", "Manly Beach", 10, COORDINATOR_ID);

        assertTrue(event.getId() > 0);
        assertEquals("Beach clean-up", event.getTitle());
        assertEquals(1, eventDAO.size());
    }

    @Test
    void createRejectsBlankTitle() {
        assertThrows(EventException.class, () -> service.create("",
                "desc", futureDate(), "09:00", "here", 5, COORDINATOR_ID));
        assertThrows(EventException.class, () -> service.create("   ",
                "desc", futureDate(), "09:00", "here", 5, COORDINATOR_ID));
    }

    @Test
    void createRejectsPastDate() {
        EventException ex = assertThrows(EventException.class,
                () -> service.create("Late event", "desc", pastDate(), "09:00",
                        "here", 5, COORDINATOR_ID));
        assertTrue(ex.getMessage().toLowerCase().contains("past"));
    }

    @Test
    void createRejectsMalformedDate() {
        assertThrows(EventException.class,
                () -> service.create("Bad date", "desc", "not-a-date", "09:00",
                        "here", 5, COORDINATOR_ID));
    }

    @Test
    void createRejectsZeroOrNegativeVolunteers() {
        assertThrows(EventException.class,
                () -> service.create("No vols", "desc", futureDate(), "09:00", "here", 0, COORDINATOR_ID));
        assertThrows(EventException.class,
                () -> service.create("No vols", "desc", futureDate(), "09:00", "here", -3, COORDINATOR_ID));
    }

    // ---------- update ----------

    @Test
    void updateChangesFieldsAndPersists() throws EventException {
        Event original = service.create("Original", "old desc", futureDate(),
                "09:00", "old loc", 5, COORDINATOR_ID);

        Event updated = service.update(original.getId(), "New Title", "new desc",
                futureDate(), "10:00", "New Loc", 12);

        assertEquals("New Title", updated.getTitle());
        assertEquals("New Loc", updated.getLocation());
        assertEquals(12, updated.getVolunteersNeeded());

        // Re-fetch to verify DAO round-trip
        Event fromDao = eventDAO.findById(original.getId()).orElseThrow();
        assertEquals("New Title", fromDao.getTitle());
    }

    @Test
    void updateFailsForUnknownEvent() {
        assertThrows(EventException.class,
                () -> service.update(9999, "x", "y", futureDate(), "10:00", "z", 3));
    }

    @Test
    void updateAppliesSameValidationRulesAsCreate() throws EventException {
        Event original = service.create("Valid", "desc", futureDate(), "09:00",
                "loc", 5, COORDINATOR_ID);

        assertThrows(EventException.class,
                () -> service.update(original.getId(), "", "d", futureDate(), "09:00", "l", 5),
                "Empty title should be rejected on update too");
        assertThrows(EventException.class,
                () -> service.update(original.getId(), "Title", "d", pastDate(), "09:00", "l", 5),
                "Past date should be rejected on update too");
    }

    // ---------- listing ----------

    @Test
    void findByCoordinatorFiltersCorrectly() throws EventException {
        service.create("Mine 1", "d", futureDate(), "09:00", "l", 5, COORDINATOR_ID);
        service.create("Mine 2", "d", futureDate(), "10:00", "l", 5, COORDINATOR_ID);
        service.create("Someone else's", "d", futureDate(), "11:00", "l", 5, 99);

        assertEquals(2, service.findByCoordinator(COORDINATOR_ID).size());
        assertEquals(1, service.findByCoordinator(99).size());
    }

    @Test
    void findAllReturnsEveryEvent() throws EventException {
        service.create("A", "d", futureDate(), "09:00", "l", 5, COORDINATOR_ID);
        service.create("B", "d", futureDate(), "10:00", "l", 5, 1);
        service.create("C", "d", futureDate(), "11:00", "l", 5, 2);

        assertEquals(3, service.findAll().size());
    }
}
