package com.cab302.vic.dao;

import com.cab302.vic.model.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs {@link SqliteSignupDAO} against a real, temporary SQLite file so the
 * SQL itself is tested, not just the service rules.
 */
class SqliteSignupDAOTest {

    private SqliteSignupDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        Path file = Files.createTempFile("vic-signups-", ".db");
        file.toFile().deleteOnExit();
        Files.delete(file);
        DatabaseManager db = new DatabaseManager("jdbc:sqlite:" + file);
        db.initialise();
        dao = new SqliteSignupDAO(db);
    }

    @Test
    void createAssignsIdAndCanBeFoundAgain() {
        Signup saved = dao.create(new Signup(0, 5, 10, false, "2026-10-20"));

        assertTrue(saved.getId() > 0);
        Signup found = dao.find(5, 10).orElseThrow();
        assertEquals(saved.getId(), found.getId());
        assertEquals("2026-10-20", found.getSignedUpOn());
        assertFalse(found.isAttended());
    }

    @Test
    void findReturnsEmptyWhenNotSignedUp() {
        assertTrue(dao.find(5, 10).isEmpty());
    }

    @Test
    void listsAndCountsByEventAndVolunteer() {
        dao.create(new Signup(0, 5, 10, false, "2026-10-20"));
        dao.create(new Signup(0, 5, 11, false, "2026-10-20"));
        dao.create(new Signup(0, 6, 10, false, "2026-10-20"));

        assertEquals(2, dao.findByEvent(5).size());
        assertEquals(2, dao.countForEvent(5));
        assertEquals(0, dao.countForEvent(7));
        assertEquals(2, dao.findByUser(10).size());
    }

    @Test
    void setAttendedUpdatesOnlyThatSignup() {
        dao.create(new Signup(0, 5, 10, false, "2026-10-20"));
        dao.create(new Signup(0, 5, 11, false, "2026-10-20"));

        assertTrue(dao.setAttended(5, 10, true));

        assertTrue(dao.find(5, 10).orElseThrow().isAttended());
        assertFalse(dao.find(5, 11).orElseThrow().isAttended());
        assertFalse(dao.setAttended(5, 99, true));
    }

    @Test
    void deleteRemovesSignup() {
        dao.create(new Signup(0, 5, 10, false, "2026-10-20"));

        assertTrue(dao.delete(5, 10));
        assertTrue(dao.find(5, 10).isEmpty());
        assertFalse(dao.delete(5, 10));
    }

    @Test
    void databaseRefusesDuplicateSignup() {
        dao.create(new Signup(0, 5, 10, false, "2026-10-20"));

        assertThrows(RuntimeException.class,
                () -> dao.create(new Signup(0, 5, 10, false, "2026-10-20")));
    }
}
