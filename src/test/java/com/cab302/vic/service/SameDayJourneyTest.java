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
 * End-to-end test of the complete volunteer journey on a single day:
 * create, sign up, attend, log hours, approve, report.
 *
 * <p>This exists because the pieces each passed their own tests while the
 * journey as a whole was impossible. Attendance originally required the
 * event date to be strictly in the past, but events cannot be created in
 * the past, so a coordinator could never reach the hours step without
 * waiting for the next calendar day. Every unit test passed and the
 * headline feature was unreachable.
 *
 * <p>Keeping the whole chain in one test means a future change to any
 * single rule that breaks the journey fails here.
 */
class SameDayJourneyTest {

    private FakeEventDAO eventDAO;
    private FakeSignupDAO signupDAO;
    private FakeHoursDAO hoursDAO;
    private EventService events;
    private SignupService signups;
    private HoursService hours;

    private static final int COORDINATOR = 1;
    private static final int VOLUNTEER = 7;

    @BeforeEach
    void setUp() {
        eventDAO = new FakeEventDAO();
        signupDAO = new FakeSignupDAO();
        hoursDAO = new FakeHoursDAO(eventDAO);
        events = new EventService(eventDAO);
        signups = new SignupService(signupDAO, eventDAO);
        hours = new HoursService(hoursDAO, signupDAO, eventDAO);
    }

    @Test
    void aCoordinatorCanRunTheWholeCycleInOneSitting() throws Exception {
        String today = LocalDate.now().toString();

        // 1. Coordinator creates an event happening today.
        Event event = events.create("Beach Clean-up", "Bring gloves",
                today, "09:00", "Manly Beach", 5, COORDINATOR);
        assertTrue(event.getId() > 0);

        // 2. A volunteer signs up. An event today is still open.
        signups.signUp(event.getId(), VOLUNTEER);
        assertTrue(signups.isSignedUp(event.getId(), VOLUNTEER));
        assertEquals(4, signups.spotsRemaining(event));

        // 3. The event runs. The coordinator marks the register the same day.
        signups.markAttendance(event.getId(), VOLUNTEER, true);
        assertTrue(signupDAO.find(event.getId(), VOLUNTEER).orElseThrow().isAttended());

        // 4. The volunteer logs their hours.
        HoursEntry claim = hours.logHours(VOLUNTEER, event.getId(), 4.5);
        assertTrue(claim.isPending());

        // 5. Nothing counts yet, because nothing has been verified.
        assertEquals(0.0, hours.totalApprovedHoursForCoordinator(COORDINATOR), 0.001,
                "unverified hours must not reach the reported total");
        assertEquals(1, hours.findPendingForCoordinator(COORDINATOR).size());

        // 6. The coordinator approves.
        hours.approve(claim.getId(), COORDINATOR);

        // 7. Now it counts, for both sides.
        assertEquals(4.5, hours.totalApprovedHoursForCoordinator(COORDINATOR), 0.001);
        assertEquals(4.5, hours.totalApprovedHoursForVolunteer(VOLUNTEER), 0.001);
        assertTrue(hours.findPendingForCoordinator(COORDINATOR).isEmpty(),
                "the queue should be clear once reviewed");
    }

    @Test
    void theRejectionPathAlsoWorksSameDay() throws Exception {
        String today = LocalDate.now().toString();
        Event event = events.create("Garden Working Bee", "Weeding",
                today, "14:00", "Kelvin Grove", 5, COORDINATOR);

        signups.signUp(event.getId(), VOLUNTEER);
        signups.markAttendance(event.getId(), VOLUNTEER, true);
        HoursEntry claim = hours.logHours(VOLUNTEER, event.getId(), 18);

        hours.reject(claim.getId(), COORDINATOR, "Event only ran for three hours");

        assertEquals(0.0, hours.totalApprovedHoursForCoordinator(COORDINATOR), 0.001);
        assertEquals("Event only ran for three hours",
                hoursDAO.findById(claim.getId()).orElseThrow().getReviewNote(),
                "the volunteer needs to see why it was rejected");
    }

    @Test
    void hoursStillCannotBeLoggedWithoutAttendance() throws Exception {
        // The same-day relaxation must not weaken the rule that protects
        // the figures: signing up is not the same as turning up.
        String today = LocalDate.now().toString();
        Event event = events.create("Beach Clean-up", "Bring gloves",
                today, "09:00", "Manly Beach", 5, COORDINATOR);

        signups.signUp(event.getId(), VOLUNTEER);   // signed up, not marked attended

        assertThrows(HoursException.class,
                () -> hours.logHours(VOLUNTEER, event.getId(), 4));
    }

    @Test
    void attendanceStillCannotBeRecordedForAFutureEvent() throws Exception {
        Event future = events.create("Next month's clean-up", "",
                LocalDate.now().plusDays(30).toString(), "09:00", "Manly Beach", 5, COORDINATOR);

        signups.signUp(future.getId(), VOLUNTEER);

        assertThrows(SignupException.class,
                () -> signups.markAttendance(future.getId(), VOLUNTEER, true),
                "a coordinator must not be able to mark a register before the event");
    }
}
