package com.cab302.vic.util;

import com.cab302.vic.model.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Turns stored event data into what the volunteer dashboard shows:
 * a readable date and start time, and the order events are listed in.
 * Kept free of JavaFX so it can be unit tested.
 */
public final class EventDisplay {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    /** Earliest date first, then earliest start time. Unreadable dates go last. */
    private static final Comparator<Event> SOONEST_FIRST =
            Comparator.comparing(EventDisplay::sortDate)
                    .thenComparing(EventDisplay::sortTime);

    private EventDisplay() {}

    /**
     * Formats when an event happens, e.g. "Tue 20 Oct 2026, 9:00 AM".
     * The time is left off when none was entered, and a date that can't be
     * read is shown exactly as stored rather than hidden.
     *
     * @param event the event
     * @return a human-readable date and start time
     */
    public static String when(Event event) {
        LocalDate date = event.parsedDate();
        String datePart = date == null ? safe(event.getEventDate()) : DATE.format(date);
        LocalTime time = parseTime(event.getEventTime());
        if (time != null) {
            return datePart + ", " + TIME.format(time);
        }
        String rawTime = safe(event.getEventTime());
        return rawTime.isEmpty() ? datePart : datePart + ", " + rawTime;
    }

    /**
     * Events that haven't happened yet, soonest first.
     *
     * @param events all events
     * @param hasPassed decides whether an event is in the past
     * @return the upcoming events in the order to show them
     */
    public static List<Event> upcoming(List<Event> events, Predicate<Event> hasPassed) {
        return events.stream()
                .filter(hasPassed.negate())
                .sorted(SOONEST_FIRST)
                .toList();
    }

    /**
     * Events that have already happened, most recent first.
     *
     * @param events all events
     * @param hasPassed decides whether an event is in the past
     * @return the past events in the order to show them
     */
    public static List<Event> past(List<Event> events, Predicate<Event> hasPassed) {
        return events.stream()
                .filter(hasPassed)
                .sorted(SOONEST_FIRST.reversed())
                .toList();
    }

    private static LocalDate sortDate(Event event) {
        LocalDate date = event.parsedDate();
        return date == null ? LocalDate.MAX : date;
    }

    private static LocalTime sortTime(Event event) {
        LocalTime time = parseTime(event.getEventTime());
        return time == null ? LocalTime.MIDNIGHT : time;
    }

    private static LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
