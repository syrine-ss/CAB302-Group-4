package com.cab302.vic.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HoursEntryTest {

    @Test
    void constructorSetsAllFields() {
        HoursEntry h = new HoursEntry(5, 20, 10, 3.5,
                HoursEntry.Status.PENDING, "", "2026-10-07");

        assertEquals(5, h.getId());
        assertEquals(20, h.getUserId());
        assertEquals(10, h.getEventId());
        assertEquals(3.5, h.getHours(), 0.0001);
        assertEquals(HoursEntry.Status.PENDING, h.getStatus());
        assertEquals("", h.getReviewNote());
        assertEquals("2026-10-07", h.getLoggedOn());
        assertTrue(h.isPending());
    }

    @Test
    void missingStatusAndNoteDefaultToPendingAndEmpty() {
        HoursEntry h = new HoursEntry(1, 20, 10, 2, null, null, "2026-10-07");

        assertEquals(HoursEntry.Status.PENDING, h.getStatus());
        assertEquals("", h.getReviewNote());
    }

    @Test
    void reviewRecordsDecisionAndTrimmedNote() {
        HoursEntry h = new HoursEntry(1, 20, 10, 2,
                HoursEntry.Status.PENDING, "", "2026-10-07");

        h.review(HoursEntry.Status.REJECTED, "  left early  ");

        assertEquals(HoursEntry.Status.REJECTED, h.getStatus());
        assertEquals("left early", h.getReviewNote());
        assertFalse(h.isPending());
    }

    @Test
    void statusFromDbReadsStoredText() {
        assertEquals(HoursEntry.Status.APPROVED, HoursEntry.Status.fromDb("APPROVED"));
        assertEquals(HoursEntry.Status.REJECTED, HoursEntry.Status.fromDb(" rejected "));
    }

    @Test
    void statusFromDbTreatsMissingValueAsPending() {
        assertEquals(HoursEntry.Status.PENDING, HoursEntry.Status.fromDb(null));
        assertEquals(HoursEntry.Status.PENDING, HoursEntry.Status.fromDb(""));
    }

    @Test
    void statusFromDbRejectsUnknownText() {
        assertThrows(IllegalArgumentException.class,
                () -> HoursEntry.Status.fromDb("MAYBE"));
    }
}
