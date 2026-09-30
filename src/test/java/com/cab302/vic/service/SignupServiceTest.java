package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeSignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour tests for {@link SignupService}.
 *
 * <p>Written test-first. The capacity rule is a good example of the cycle:
 * {@code signUpFailsWhenEventIsFull} was written and watched fail (Red)
 * because the first version of {@code signUp} only checked for duplicates.
 * The capacity guard was added to make it pass (Green), and the shared
 * "is this event full" logic was then pulled out into {@link
 * SignupService#isFull} so both the service and the UI could use it
 * (Refactor).
 */
class SignupServiceTest {

    private FakeSignupDAO signupDAO;
    private FakeEventDAO eventDAO;
    private SignupService service;

    private static final int COORDINATOR = 1;
    private static final int VOLUNTEER = 7;

    @BeforeEach
    void setUp() {
        signupDAO = new FakeSignupDAO();
        eventDAO = new FakeEventDAO();
        service = new SignupService(signupDAO, eventDAO);
    }

    private Event upcomingEvent(int capacity) {
        return eventDAO.create(Event.builder()
                .title("Beach Clean-up")
                .description("Bring gloves")
                .date(LocalDate.now().plusDays(14).toString())
                .time("09:00")
                .location("Manly Beach")
                .volunteersNeeded(capacity)
                .createdBy(COORDINATOR)
                .build());
    }

    private Event pastEvent(int capacity) {
        return eventDAO.create(Event.builder()
                .title("Last month's working bee")
                .date(LocalDate.now().minusDays(20).toString())
                .volunteersNeeded(capacity)
                .createdBy(COORDINATOR)
                .build());
    }

    // ---------- signing up ----------

    @Test
    void signUpRecordsTheVolunteerAgainstTheEvent() throws SignupException {
        Event event = upcomingEvent(10);

        Signup signup = service.signUp(event.getId(), VOLUNTEER);

        assertTrue(signup.getId() > 0, "a persisted signup should have an id");
        assertEquals(event.getId(), signup.getEventId());
        assertEquals(VOLUNTEER, signup.getUserId());
        assertEquals(1, signupDAO.size());
    }

    @Test
    void signUpStartsWithAttendanceUnconfirmed() throws SignupException {
        Event event = upcomingEvent(10);

        Signup signup = service.signUp(event.getId(), VOLUNTEER);

        assertFalse(signup.isAttended(),
                "signing up is a statement of intent, not proof of attendance");
    }

    @Test
    void signUpFailsWhenAlreadySignedUp() throws SignupException {
        Event event = upcomingEvent(10);
        service.signUp(event.getId(), VOLUNTEER);

        SignupException ex = assertThrows(SignupException.class,
                () -> service.signUp(event.getId(), VOLUNTEER));

        assertTrue(ex.getMessage().toLowerCase().contains("already signed up"));
        assertEquals(1, signupDAO.size(), "the duplicate must not be stored");
    }

    @Test
    void signUpFailsWhenEventIsFull() throws SignupException {
        Event event = upcomingEvent(2);
        service.signUp(event.getId(), 101);
        service.signUp(event.getId(), 102);

        SignupException ex = assertThrows(SignupException.class,
                () -> service.signUp(event.getId(), 103));

        assertTrue(ex.getMessage().toLowerCase().contains("full"));
        assertEquals(2, signupDAO.size());
    }

    @Test
    void signUpFailsForAnEventThatHasAlreadyHappened() {
        Event event = pastEvent(10);

        SignupException ex = assertThrows(SignupException.class,
                () -> service.signUp(event.getId(), VOLUNTEER));

        assertTrue(ex.getMessage().toLowerCase().contains("already happened"));
    }

    @Test
    void signUpFailsForAnEventThatDoesNotExist() {
        assertThrows(SignupException.class, () -> service.signUp(9999, VOLUNTEER));
    }

    // ---------- withdrawing ----------

    @Test
    void withdrawRemovesTheSignupAndFreesTheSpot() throws SignupException {
        Event event = upcomingEvent(1);
        service.signUp(event.getId(), VOLUNTEER);
        assertTrue(service.isFull(event));

        service.withdraw(event.getId(), VOLUNTEER);

        assertEquals(0, signupDAO.size());
        assertFalse(service.isFull(event), "withdrawing should free the place for someone else");
    }

    @Test
    void withdrawFailsWhenNotSignedUp() {
        Event event = upcomingEvent(10);
        assertThrows(SignupException.class, () -> service.withdraw(event.getId(), VOLUNTEER));
    }

    @Test
    void withdrawFailsOnceAttendanceHasBeenRecorded() throws SignupException {
        Event event = pastEvent(10);
        // Sign up directly through the DAO, since the service blocks signups
        // to past events, then have the coordinator confirm attendance.
        signupDAO.create(Signup.createNew(event.getId(), VOLUNTEER));
        service.markAttendance(event.getId(), VOLUNTEER, true);

        SignupException ex = assertThrows(SignupException.class,
                () -> service.withdraw(event.getId(), VOLUNTEER));

        assertTrue(ex.getMessage().toLowerCase().contains("marked as attending"));
        assertEquals(1, signupDAO.size(), "the attendance record must survive");
    }

    // ---------- attendance ----------

    @Test
    void markAttendanceRecordsThatTheVolunteerTurnedUp() throws SignupException {
        Event event = pastEvent(10);
        signupDAO.create(Signup.createNew(event.getId(), VOLUNTEER));

        Signup updated = service.markAttendance(event.getId(), VOLUNTEER, true);

        assertTrue(updated.isAttended());
        assertTrue(signupDAO.find(event.getId(), VOLUNTEER).orElseThrow().isAttended(),
                "the change must be persisted, not only held in memory");
    }

    @Test
    void markAttendanceFailsBeforeTheEventHasHappened() throws SignupException {
        Event event = upcomingEvent(10);
        service.signUp(event.getId(), VOLUNTEER);

        SignupException ex = assertThrows(SignupException.class,
                () -> service.markAttendance(event.getId(), VOLUNTEER, true));

        assertTrue(ex.getMessage().toLowerCase().contains("after the event date"));
    }

    @Test
    void markAttendanceFailsForSomeoneWhoNeverSignedUp() {
        Event event = pastEvent(10);
        assertThrows(SignupException.class,
                () -> service.markAttendance(event.getId(), 404, true));
    }

    // ---------- capacity reporting ----------

    @Test
    void spotsRemainingCountsDownAsVolunteersJoin() throws SignupException {
        Event event = upcomingEvent(3);
        assertEquals(3, service.spotsRemaining(event));

        service.signUp(event.getId(), 101);
        assertEquals(2, service.spotsRemaining(event));

        service.signUp(event.getId(), 102);
        service.signUp(event.getId(), 103);
        assertEquals(0, service.spotsRemaining(event));
    }

    @Test
    void spotsRemainingNeverGoesNegative() {
        // A coordinator can lower the target below the number already signed up.
        Event event = upcomingEvent(1);
        signupDAO.create(Signup.createNew(event.getId(), 101));
        signupDAO.create(Signup.createNew(event.getId(), 102));

        assertEquals(0, service.spotsRemaining(event),
                "an over-subscribed event should read as zero left, not a negative number");
    }

    @Test
    void signupsAreScopedToTheirOwnEvent() throws SignupException {
        Event first = upcomingEvent(10);
        Event second = upcomingEvent(10);

        service.signUp(first.getId(), 101);
        service.signUp(first.getId(), 102);
        service.signUp(second.getId(), 103);

        assertEquals(2, service.findByEvent(first.getId()).size());
        assertEquals(1, service.findByEvent(second.getId()).size());
    }
}
