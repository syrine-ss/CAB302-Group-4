package com.cab302.vic.dao;

import com.cab302.vic.model.HoursEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Runs {@link SqliteHoursDAO} against a real, temporary SQLite file. */
class SqliteHoursDAOTest {

    private SqliteHoursDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        Path file = Files.createTempFile("vic-hours-", ".db");
        file.toFile().deleteOnExit();
        Files.delete(file);
        DatabaseManager db = new DatabaseManager("jdbc:sqlite:" + file);
        db.initialise();
        dao = new SqliteHoursDAO(db);
    }

    private static HoursEntry pending(int userId, int eventId, double hours) {
        return new HoursEntry(0, userId, eventId, hours, HoursEntry.Status.PENDING, "", "2026-10-20");
    }

    @Test
    void createAssignsIdAndCanBeFoundAgain() {
        HoursEntry saved = dao.create(pending(10, 5, 3.5));

        HoursEntry found = dao.findById(saved.getId()).orElseThrow();
        assertEquals(10, found.getUserId());
        assertEquals(5, found.getEventId());
        assertEquals(3.5, found.getHours(), 0.0001);
        assertEquals(HoursEntry.Status.PENDING, found.getStatus());
        assertEquals("2026-10-20", found.getLoggedOn());
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertTrue(dao.findById(42).isEmpty());
    }

    @Test
    void listsByEventAndVolunteer() {
        dao.create(pending(10, 5, 1));
        dao.create(pending(11, 5, 2));
        dao.create(pending(10, 6, 3));

        assertEquals(2, dao.findByEvent(5).size());
        assertEquals(2, dao.findByUser(10).size());
        assertEquals(0, dao.findByUser(99).size());
    }

    @Test
    void updateReviewSavesStatusAndNote() {
        HoursEntry entry = dao.create(pending(10, 5, 2));
        entry.review(HoursEntry.Status.REJECTED, "Wrong event");

        assertTrue(dao.updateReview(entry));

        HoursEntry found = dao.findById(entry.getId()).orElseThrow();
        assertEquals(HoursEntry.Status.REJECTED, found.getStatus());
        assertEquals("Wrong event", found.getReviewNote());
    }

    @Test
    void updateReviewReturnsFalseForUnknownEntry() {
        HoursEntry ghost = new HoursEntry(999, 10, 5, 2, HoursEntry.Status.APPROVED, "", "2026-10-20");

        assertFalse(dao.updateReview(ghost));
    }
}
