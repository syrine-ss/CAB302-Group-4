package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeHoursDAO;
import com.cab302.vic.dao.FakeSignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests of the main volunteer journey across all three services:
 * a coordinator creates an event, a volunteer signs up, the coordinator marks
 * attendance, the volunteer logs hours and the coordinator approves them.
 *
 * <p>Each service has its own unit tests, but the rules only make sense
 * together. For example, an event can't be created in the past, so attendance
 * and hours must work on the day of the event or the feature can never be used.
 * These tests check that the whole chain works, and that no step can be
 * skipped.
 *
 * <p>The services share the same fake DAOs, just as they share one database
 * in the app.
 */
class VolunteerJourneyTest {

    private static final int COORDINATOR = 1;
    private static final int VOLUNTEER = 10;

    private EventService events;
    private SignupService signups;
    private HoursService hours;

    @BeforeEach
    void setUp() {
        FakeEventDAO eventDAO = new FakeEventDAO();
        FakeSignupDAO signupDAO = new FakeSignupDAO();
        FakeHoursDAO hoursDAO = new FakeHoursDAO();
        events = new EventService(eventDAO);
        signups = new SignupService(signupDAO, eventDAO);
        hours = new HoursService(hoursDAO, signupDAO, eventDAO);
    }

    private Event createEventToday() throws EventException {
        return events.create("Community garden working bee", "Bring gloves",
                LocalDate.now().toString(), "09:00", "Community garden", 5, COORDINATOR);
    }

    @Test
    void wholeJourneyWorksOnTheDayOfTheEvent() throws Exception {
        Event event = createEventToday();

        signups.signUp(event.getId(), VOLUNTEER);
        assertTrue(signups.isSignedUp(event.getId(), VOLUNTEER));
        assertEquals(4, signups.spotsLeft(event));

        signups.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR);

        HoursEntry entry = hours.logHours(event.getId(), VOLUNTEER, 3);
        assertEquals(HoursEntry.Status.PENDING, entry.getStatus());
        assertEquals(1, hours.pendingForCoordinator(COORDINATOR).size());
        assertEquals(0.0, hours.totalApprovedHours(VOLUNTEER), 0.0001);

        hours.approve(entry.getId(), COORDINATOR, "Thanks for coming");

        assertEquals(3.0, hours.totalApprovedHours(VOLUNTEER), 0.0001);
        assertTrue(hours.pendingForCoordinator(COORDINATOR).isEmpty());
    }

    @Test
    void hoursCannotBeLoggedBeforeAttendanceIsMarked() throws Exception {
        Event event = createEventToday();
        signups.signUp(event.getId(), VOLUNTEER);

        assertThrows(HoursException.class, () -> hours.logHours(event.getId(), VOLUNTEER, 3));
    }

    @Test
    void attendanceCannotBeMarkedBeforeTheEvent() throws Exception {
        Event event = events.create("Beach clean-up", "", LocalDate.now().plusDays(7).toString(),
                "09:00", "Beach", 5, COORDINATOR);
        signups.signUp(event.getId(), VOLUNTEER);

        assertThrows(SignupException.class,
                () -> signups.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR));
        assertThrows(HoursException.class, () -> hours.logHours(event.getId(), VOLUNTEER, 3));
    }

    @Test
    void volunteerCannotWithdrawOnceTheyHaveAttended() throws Exception {
        Event event = createEventToday();
        signups.signUp(event.getId(), VOLUNTEER);
        signups.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR);
        hours.logHours(event.getId(), VOLUNTEER, 2);

        assertThrows(SignupException.class, () -> signups.withdraw(event.getId(), VOLUNTEER));
        assertEquals(1, hours.entriesForVolunteer(VOLUNTEER).size());
    }

    @Test
    void rejectedHoursCanBeCorrectedAndApproved() throws Exception {
        Event event = createEventToday();
        signups.signUp(event.getId(), VOLUNTEER);
        signups.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR);

        HoursEntry first = hours.logHours(event.getId(), VOLUNTEER, 8);
        hours.reject(first.getId(), COORDINATOR, "The event ran for 3 hours");
        HoursEntry corrected = hours.logHours(event.getId(), VOLUNTEER, 3);
        hours.approve(corrected.getId(), COORDINATOR, null);

        assertEquals(3.0, hours.totalApprovedHours(VOLUNTEER), 0.0001);
        assertEquals(2, hours.entriesForVolunteer(VOLUNTEER).size());
    }
}
