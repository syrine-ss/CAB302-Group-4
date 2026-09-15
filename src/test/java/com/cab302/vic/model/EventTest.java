package com.cab302.vic.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void constructorSetsAllFields() {
        Event e = new Event(1, "Cleanup", "bring gloves", "2026-09-20", "09:00",
                "Manly Beach", 10, 42);

        assertEquals(1, e.getId());
        assertEquals("Cleanup", e.getTitle());
        assertEquals("bring gloves", e.getDescription());
        assertEquals("2026-09-20", e.getEventDate());
        assertEquals("09:00", e.getEventTime());
        assertEquals("Manly Beach", e.getLocation());
        assertEquals(10, e.getVolunteersNeeded());
        assertEquals(42, e.getCreatedBy());
    }

    @Test
    void parsedDateReturnsLocalDateForIsoFormat() {
        Event e = new Event(1, "T", "d", "2026-09-20", "09:00", "l", 5, 1);
        assertEquals(LocalDate.of(2026, 9, 20), e.parsedDate());
    }

    @Test
    void parsedDateReturnsNullForBadInput() {
        Event e = new Event(1, "T", "d", "not-a-date", "09:00", "l", 5, 1);
        assertNull(e.parsedDate());
    }

    @Test
    void equalityBasedOnId() {
        Event a = new Event(1, "A", "d", "2026-09-20", "09:00", "l", 5, 1);
        Event b = new Event(1, "B", "different", "2026-09-21", "10:00", "l2", 6, 2);
        Event c = new Event(2, "A", "d", "2026-09-20", "09:00", "l", 5, 1);

        assertEquals(a, b, "Events with the same id are equal");
        assertNotEquals(a, c, "Events with different ids are not equal");
    }
}
