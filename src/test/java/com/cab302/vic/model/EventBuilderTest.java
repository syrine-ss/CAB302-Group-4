package com.cab302.vic.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Event.Builder} introduced when the eight-argument
 * constructor was refactored.
 *
 * <p>The constructor takes four consecutive String parameters, so a caller
 * could silently swap location and time and the compiler would not notice.
 * These tests pin the behaviour the Builder guarantees instead.
 */
class EventBuilderTest {

    @Test
    void builderSetsEveryFieldByName() {
        Event event = Event.builder()
                .id(5)
                .title("Beach Clean-up")
                .description("Bring gloves")
                .date("2026-11-20")
                .time("09:00")
                .location("Manly Beach")
                .volunteersNeeded(12)
                .createdBy(3)
                .build();

        assertEquals(5, event.getId());
        assertEquals("Beach Clean-up", event.getTitle());
        assertEquals("Bring gloves", event.getDescription());
        assertEquals("2026-11-20", event.getEventDate());
        assertEquals("09:00", event.getEventTime());
        assertEquals("Manly Beach", event.getLocation());
        assertEquals(12, event.getVolunteersNeeded());
        assertEquals(3, event.getCreatedBy());
    }

    @Test
    void optionalTextFieldsDefaultToEmptyRatherThanNull() {
        // The UI renders these directly, so a null would surface as the
        // literal text "null" on screen.
        Event event = Event.builder().title("Minimal").date("2026-11-20").build();

        assertEquals("", event.getDescription());
        assertEquals("", event.getEventTime());
        assertEquals("", event.getLocation());
    }

    @Test
    void nullTextIsNormalisedToEmpty() {
        Event event = Event.builder()
                .title("Has nulls")
                .date("2026-11-20")
                .description(null)
                .time(null)
                .location(null)
                .build();

        assertEquals("", event.getDescription());
        assertEquals("", event.getEventTime());
        assertEquals("", event.getLocation());
    }

    @Test
    void volunteersNeededDefaultsToOne() {
        Event event = Event.builder().title("Minimal").date("2026-11-20").build();
        assertEquals(1, event.getVolunteersNeeded(),
                "an event needs at least one volunteer to be worth running");
    }

    @Test
    void builderFromCopiesAnExistingEventForEditing() {
        Event original = Event.builder()
                .id(9)
                .title("Original title")
                .description("Original description")
                .date("2026-11-20")
                .time("09:00")
                .location("Manly Beach")
                .volunteersNeeded(12)
                .createdBy(3)
                .build();

        Event edited = Event.builderFrom(original)
                .title("Updated title")
                .location("Coolangatta Beach")
                .build();

        // Changed fields
        assertEquals("Updated title", edited.getTitle());
        assertEquals("Coolangatta Beach", edited.getLocation());
        // Everything else carried over untouched
        assertEquals(9, edited.getId());
        assertEquals("Original description", edited.getDescription());
        assertEquals("2026-11-20", edited.getEventDate());
        assertEquals("09:00", edited.getEventTime());
        assertEquals(12, edited.getVolunteersNeeded());
        assertEquals(3, edited.getCreatedBy());
    }

    @Test
    void builderProducesTheSameObjectAsTheConstructor() {
        // Guards the refactoring itself: the Builder must be a drop-in
        // replacement, not a subtly different object.
        Event viaConstructor = new Event(1, "T", "D", "2026-11-20", "09:00", "L", 4, 2);
        Event viaBuilder = Event.builder()
                .id(1).title("T").description("D").date("2026-11-20")
                .time("09:00").location("L").volunteersNeeded(4).createdBy(2)
                .build();

        assertEquals(viaConstructor.getId(), viaBuilder.getId());
        assertEquals(viaConstructor.getTitle(), viaBuilder.getTitle());
        assertEquals(viaConstructor.getDescription(), viaBuilder.getDescription());
        assertEquals(viaConstructor.getEventDate(), viaBuilder.getEventDate());
        assertEquals(viaConstructor.getEventTime(), viaBuilder.getEventTime());
        assertEquals(viaConstructor.getLocation(), viaBuilder.getLocation());
        assertEquals(viaConstructor.getVolunteersNeeded(), viaBuilder.getVolunteersNeeded());
        assertEquals(viaConstructor.getCreatedBy(), viaBuilder.getCreatedBy());
        assertEquals(viaConstructor, viaBuilder);
    }
}
