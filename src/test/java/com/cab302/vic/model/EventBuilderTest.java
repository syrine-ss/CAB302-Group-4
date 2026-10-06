package com.cab302.vic.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Event.Builder}. Written before the builder existed, then
 * used to confirm the refactored {@code EventService} and {@code SqliteEventDAO}
 * still produce the same events as the old constructor.
 */
class EventBuilderTest {

    @Test
    void buildsEventWithEveryField() {
        Event e = Event.builder()
                .id(7)
                .title("Beach clean-up")
                .description("Bring gloves")
                .date("2026-10-20")
                .time("09:00")
                .location("Manly Beach")
                .volunteersNeeded(10)
                .createdBy(42)
                .build();

        assertEquals(7, e.getId());
        assertEquals("Beach clean-up", e.getTitle());
        assertEquals("Bring gloves", e.getDescription());
        assertEquals("2026-10-20", e.getEventDate());
        assertEquals("09:00", e.getEventTime());
        assertEquals("Manly Beach", e.getLocation());
        assertEquals(10, e.getVolunteersNeeded());
        assertEquals(42, e.getCreatedBy());
    }

    @Test
    void matchesTheEquivalentConstructorCall() {
        Event fromConstructor = new Event(7, "Beach clean-up", "Bring gloves",
                "2026-10-20", "09:00", "Manly Beach", 10, 42);
        Event fromBuilder = Event.builder()
                .id(7).title("Beach clean-up").description("Bring gloves")
                .date("2026-10-20").time("09:00").location("Manly Beach")
                .volunteersNeeded(10).createdBy(42)
                .build();

        assertEquals(fromConstructor, fromBuilder);
        assertEquals(fromConstructor.getEventTime(), fromBuilder.getEventTime());
        assertEquals(fromConstructor.getLocation(), fromBuilder.getLocation());
    }

    @Test
    void fieldsCanBeSetInAnyOrder() {
        Event e = Event.builder()
                .createdBy(1)
                .date("2026-10-20")
                .title("Working bee")
                .build();

        assertEquals("Working bee", e.getTitle());
        assertEquals("2026-10-20", e.getEventDate());
        assertEquals(1, e.getCreatedBy());
    }

    @Test
    void newEventHasNoIdUntilSaved() {
        Event e = Event.builder().title("Working bee").date("2026-10-20").build();

        assertEquals(0, e.getId());
    }

    @Test
    void optionalTextDefaultsToEmptyRatherThanNull() {
        Event e = Event.builder()
                .title("Working bee")
                .date("2026-10-20")
                .description(null)
                .build();

        assertEquals("", e.getDescription());
        assertEquals("", e.getEventTime());
        assertEquals("", e.getLocation());
    }

    @Test
    void refusesEventWithoutTitle() {
        assertThrows(IllegalStateException.class,
                () -> Event.builder().date("2026-10-20").build());
        assertThrows(IllegalStateException.class,
                () -> Event.builder().title("  ").date("2026-10-20").build());
    }

    @Test
    void refusesEventWithoutDate() {
        assertThrows(IllegalStateException.class,
                () -> Event.builder().title("Working bee").build());
    }

    @Test
    void eachBuildCreatesASeparateEvent() {
        Event.Builder builder = Event.builder().title("Working bee").date("2026-10-20");

        Event first = builder.build();
        Event second = builder.build();

        assertNotSame(first, second);
    }
}
