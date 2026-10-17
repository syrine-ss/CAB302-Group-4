package com.cab302.vic.util;

import com.cab302.vic.model.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EventDisplay}, added after the heuristic review found the
 * volunteer dashboard showed raw ISO dates with no start time (finding V1)
 * and mixed past and upcoming events, oldest first (finding V2).
 */
class EventDisplayTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 15);
    private static final Predicate<Event> PASSED = e -> e.parsedDate().isBefore(TODAY);

    private static Event event(String title, String date, String time) {
        return new Event(0, title, "", date, time, "Brisbane", 5, 1);
    }

    private static List<String> titles(List<Event> events) {
        return events.stream().map(Event::getTitle).toList();
    }

    @Test
    void whenShowsReadableDateAndStartTime() {
        assertEquals("Tue 20 Oct 2026, 9:00 AM", EventDisplay.when(event("Beach Clean", "2026-10-20", "09:00")));
        assertEquals("Sat 31 Oct 2026, 2:30 PM", EventDisplay.when(event("Food Drive", "2026-10-31", "14:30")));
    }

    @Test
    void whenLeavesOffTimeIfNoneWasEntered() {
        assertEquals("Tue 20 Oct 2026", EventDisplay.when(event("Beach Clean", "2026-10-20", "")));
        assertEquals("Tue 20 Oct 2026", EventDisplay.when(event("Beach Clean", "2026-10-20", null)));
    }

    @Test
    void whenFallsBackToStoredTextIfDateOrTimeCannotBeRead() {
        assertEquals("next week, morning", EventDisplay.when(event("Odd", "next week", "morning")));
    }

    @Test
    void upcomingListsOnlyFutureAndTodaysEventsSoonestFirst() {
        List<Event> all = List.of(
                event("Later", "2026-11-02", "09:00"),
                event("Old", "2026-09-01", "09:00"),
                event("Today afternoon", "2026-10-15", "13:00"),
                event("Today morning", "2026-10-15", "08:00"));

        assertEquals(List.of("Today morning", "Today afternoon", "Later"),
                titles(EventDisplay.upcoming(all, PASSED)));
    }

    @Test
    void pastListsOnlyFinishedEventsMostRecentFirst() {
        List<Event> all = List.of(
                event("September", "2026-09-01", "09:00"),
                event("Upcoming", "2026-11-02", "09:00"),
                event("Last week", "2026-10-08", "09:00"));

        assertEquals(List.of("Last week", "September"),
                titles(EventDisplay.past(all, PASSED)));
    }

    @Test
    void upcomingAndPastTogetherContainEveryEventOnce() {
        List<Event> all = List.of(
                event("A", "2026-10-14", "09:00"),
                event("B", "2026-10-15", "09:00"),
                event("C", "2026-10-16", "09:00"));
        assertEquals(3, EventDisplay.upcoming(all, PASSED).size() + EventDisplay.past(all, PASSED).size());
    }
}
