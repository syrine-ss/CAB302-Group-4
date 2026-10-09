package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeSignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour tests for {@link SignupService}. The clock is fixed to
 * 20 October 2026 so "past", "today" and "future" never depend on when the
 * tests run.
 */
class SignupServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
    private static final int COORDINATOR = 1;
    private static final int OTHER_COORDINATOR = 2;
    private static final int VOLUNTEER = 10;
    private static final int OTHER_VOLUNTEER = 11;

    private FakeSignupDAO signupDAO;
    private FakeEventDAO eventDAO;
    private SignupService service;

    @BeforeEach
    void setUp() {
        signupDAO = new FakeSignupDAO();
        eventDAO = new FakeEventDAO();
        Clock fixed = Clock.fixed(TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        service = new SignupService(signupDAO, eventDAO, fixed);
    }

    private Event eventOn(LocalDate date, int volunteersNeeded) {
        return eventDAO.create(new Event(0, "Working bee", "", date.toString(), "09:00",
                "Community garden", volunteersNeeded, COORDINATOR));
    }

    private Event futureEvent() {
        return eventOn(TODAY.plusDays(7), 5);
    }

    // ---------- signing up ----------

    @Test
    void signUpCreatesSignupDatedToday() throws SignupException {
        Event event = futureEvent();

        Signup signup = service.signUp(event.getId(), VOLUNTEER);

        assertTrue(signup.getId() > 0);
        assertEquals(event.getId(), signup.getEventId());
        assertEquals(VOLUNTEER, signup.getUserId());
        assertFalse(signup.isAttended());
        assertEquals(TODAY.toString(), signup.getSignedUpOn());
        assertTrue(service.isSignedUp(event.getId(), VOLUNTEER));
    }

    @Test
    void signUpIsAllowedOnTheDayOfTheEvent() throws SignupException {
        Event event = eventOn(TODAY, 5);

        service.signUp(event.getId(), VOLUNTEER);

        assertTrue(service.isSignedUp(event.getId(), VOLUNTEER));
    }

    @Test
    void signUpRejectsEventThatHasHappened() {
        Event event = eventOn(TODAY.minusDays(1), 5);

        assertThrows(SignupException.class, () -> service.signUp(event.getId(), VOLUNTEER));
        assertEquals(0, signupDAO.size());
    }

    @Test
    void signUpRejectsSigningUpTwice() throws SignupException {
        Event event = futureEvent();
        service.signUp(event.getId(), VOLUNTEER);

        assertThrows(SignupException.class, () -> service.signUp(event.getId(), VOLUNTEER));
        assertEquals(1, signupDAO.size());
    }

    @Test
    void signUpRejectsFullEvent() throws SignupException {
        Event event = eventOn(TODAY.plusDays(7), 1);
        service.signUp(event.getId(), VOLUNTEER);

        SignupException e = assertThrows(SignupException.class,
                () -> service.signUp(event.getId(), OTHER_VOLUNTEER));
        assertEquals("This event is full", e.getMessage());
    }

    @Test
    void signUpRejectsUnknownEvent() {
        assertThrows(SignupException.class, () -> service.signUp(999, VOLUNTEER));
    }

    @Test
    void spotsLeftCountsDownAndNeverGoesNegative() throws SignupException {
        Event event = eventOn(TODAY.plusDays(7), 2);
        assertEquals(2, service.spotsLeft(event));

        service.signUp(event.getId(), VOLUNTEER);
        assertEquals(1, service.spotsLeft(event));

        service.signUp(event.getId(), OTHER_VOLUNTEER);
        assertEquals(0, service.spotsLeft(event));

        event.setVolunteersNeeded(1);
        assertEquals(0, service.spotsLeft(event));
    }

    @Test
    void hasPassedIsFalseOnTheDayAndTrueAfter() {
        assertFalse(service.hasPassed(eventOn(TODAY.plusDays(1), 5)));
        assertFalse(service.hasPassed(eventOn(TODAY, 5)));
        assertTrue(service.hasPassed(eventOn(TODAY.minusDays(1), 5)));
    }

    // ---------- withdrawing ----------

    @Test
    void withdrawRemovesSignupAndFreesTheSpot() throws SignupException {
        Event event = eventOn(TODAY.plusDays(7), 1);
        service.signUp(event.getId(), VOLUNTEER);

        service.withdraw(event.getId(), VOLUNTEER);

        assertFalse(service.isSignedUp(event.getId(), VOLUNTEER));
        assertEquals(1, service.spotsLeft(event));
    }

    @Test
    void withdrawRejectsVolunteerWhoIsNotSignedUp() {
        Event event = futureEvent();

        assertThrows(SignupException.class, () -> service.withdraw(event.getId(), VOLUNTEER));
    }

    @Test
    void withdrawRejectsEventThatHasHappened() throws SignupException {
        Event event = futureEvent();
        service.signUp(event.getId(), VOLUNTEER);
        event.setEventDate(TODAY.minusDays(1).toString());

        assertThrows(SignupException.class, () -> service.withdraw(event.getId(), VOLUNTEER));
        assertTrue(service.isSignedUp(event.getId(), VOLUNTEER));
    }

    @Test
    void withdrawRejectsOnceAttendanceIsRecorded() throws SignupException {
        Event event = eventOn(TODAY, 5);
        service.signUp(event.getId(), VOLUNTEER);
        service.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR);

        assertThrows(SignupException.class, () -> service.withdraw(event.getId(), VOLUNTEER));
        assertTrue(service.isSignedUp(event.getId(), VOLUNTEER));
    }

    // ---------- attendance ----------

    @Test
    void coordinatorCanMarkAndUnmarkAttendanceOnTheDay() throws SignupException {
        Event event = eventOn(TODAY, 5);
        service.signUp(event.getId(), VOLUNTEER);

        service.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR);
        assertTrue(signupDAO.find(event.getId(), VOLUNTEER).orElseThrow().isAttended());

        service.setAttendance(event.getId(), VOLUNTEER, false, COORDINATOR);
        assertFalse(signupDAO.find(event.getId(), VOLUNTEER).orElseThrow().isAttended());
    }

    @Test
    void attendanceCanBeRecordedAfterTheEvent() throws SignupException {
        Event event = futureEvent();
        service.signUp(event.getId(), VOLUNTEER);
        event.setEventDate(TODAY.minusDays(3).toString());

        service.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR);

        assertTrue(signupDAO.find(event.getId(), VOLUNTEER).orElseThrow().isAttended());
    }

    @Test
    void attendanceRejectsFutureEvent() throws SignupException {
        Event event = futureEvent();
        service.signUp(event.getId(), VOLUNTEER);

        assertFalse(service.canRecordAttendance(event));
        assertThrows(SignupException.class,
                () -> service.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR));
    }

    @Test
    void attendanceRejectsCoordinatorWhoDidNotCreateTheEvent() throws SignupException {
        Event event = eventOn(TODAY, 5);
        service.signUp(event.getId(), VOLUNTEER);

        assertThrows(SignupException.class,
                () -> service.setAttendance(event.getId(), VOLUNTEER, true, OTHER_COORDINATOR));
        assertFalse(signupDAO.find(event.getId(), VOLUNTEER).orElseThrow().isAttended());
    }

    @Test
    void attendanceRejectsVolunteerWhoIsNotSignedUp() {
        Event event = eventOn(TODAY, 5);

        assertThrows(SignupException.class,
                () -> service.setAttendance(event.getId(), VOLUNTEER, true, COORDINATOR));
    }

    // ---------- listing ----------

    @Test
    void listsSignupsByEventAndByVolunteer() throws SignupException {
        Event first = futureEvent();
        Event second = futureEvent();
        service.signUp(first.getId(), VOLUNTEER);
        service.signUp(first.getId(), OTHER_VOLUNTEER);
        service.signUp(second.getId(), VOLUNTEER);

        assertEquals(2, service.signupsForEvent(first.getId()).size());
        assertEquals(1, service.signupsForEvent(second.getId()).size());
        assertEquals(2, service.signupsForVolunteer(VOLUNTEER).size());
        assertEquals(1, service.signupsForVolunteer(OTHER_VOLUNTEER).size());
    }
}
