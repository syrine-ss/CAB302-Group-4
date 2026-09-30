package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeHoursDAO;
import com.cab302.vic.dao.FakeSignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour tests for {@link HoursService}.
 *
 * <p>The rules here exist to protect the impact figures shown to funders,
 * so most of these tests assert that unverified hours cannot reach the
 * totals rather than simply that the code runs.
 */
class HoursServiceTest {

    private FakeHoursDAO hoursDAO;
    private FakeSignupDAO signupDAO;
    private FakeEventDAO eventDAO;
    private HoursService service;

    private static final int COORDINATOR = 1;
    private static final int OTHER_COORDINATOR = 2;
    private static final int VOLUNTEER = 7;

    @BeforeEach
    void setUp() {
        eventDAO = new FakeEventDAO();
        hoursDAO = new FakeHoursDAO(eventDAO);
        signupDAO = new FakeSignupDAO();
        service = new HoursService(hoursDAO, signupDAO, eventDAO);
    }

    /** A past event owned by the given coordinator. */
    private Event pastEvent(int coordinatorId) {
        return eventDAO.create(Event.builder()
                .title("Community Garden Working Bee")
                .date(LocalDate.now().minusDays(7).toString())
                .location("Kelvin Grove")
                .volunteersNeeded(10)
                .createdBy(coordinatorId)
                .build());
    }

    /** Sign the volunteer up and mark them as having attended. */
    private void attended(Event event, int volunteerId) {
        Signup signup = signupDAO.create(Signup.createNew(event.getId(), volunteerId));
        signup.setAttended(true);
        signupDAO.update(signup);
    }

    // ---------- logging ----------

    @Test
    void logHoursCreatesAPendingEntry() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);

        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4.5);

        assertTrue(entry.getId() > 0);
        assertEquals(4.5, entry.getHours(), 0.001);
        assertEquals(HoursEntry.Status.PENDING, entry.getStatus(),
                "new claims must await review, never count immediately");
    }

    @Test
    void logHoursFailsWhenTheVolunteerNeverSignedUp() {
        Event event = pastEvent(COORDINATOR);

        HoursException ex = assertThrows(HoursException.class,
                () -> service.logHours(VOLUNTEER, event.getId(), 3));

        assertTrue(ex.getMessage().toLowerCase().contains("signed up"));
    }

    @Test
    void logHoursFailsWhenAttendanceWasNotConfirmed() {
        Event event = pastEvent(COORDINATOR);
        signupDAO.create(Signup.createNew(event.getId(), VOLUNTEER)); // signed up, did not attend

        HoursException ex = assertThrows(HoursException.class,
                () -> service.logHours(VOLUNTEER, event.getId(), 3));

        assertTrue(ex.getMessage().toLowerCase().contains("attended"));
    }

    @Test
    void logHoursRejectsZeroAndNegativeAmounts() {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);

        assertThrows(HoursException.class, () -> service.logHours(VOLUNTEER, event.getId(), 0));
        assertThrows(HoursException.class, () -> service.logHours(VOLUNTEER, event.getId(), -2));
    }

    @Test
    void logHoursRejectsImplausiblyLargeAmounts() {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);

        HoursException ex = assertThrows(HoursException.class,
                () -> service.logHours(VOLUNTEER, event.getId(), 25));

        assertTrue(ex.getMessage().toLowerCase().contains("exceed"));
    }

    @Test
    void logHoursFailsWhenAlreadyClaimedForThatEvent() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        service.logHours(VOLUNTEER, event.getId(), 4);

        HoursException ex = assertThrows(HoursException.class,
                () -> service.logHours(VOLUNTEER, event.getId(), 6));

        assertTrue(ex.getMessage().toLowerCase().contains("already logged"));
        assertEquals(1, hoursDAO.size(), "a second claim must not be stored");
    }

    // ---------- editing ----------

    @Test
    void updateHoursChangesAPendingClaim() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);

        HoursEntry updated = service.updateHours(entry.getId(), VOLUNTEER, 6.5);

        assertEquals(6.5, updated.getHours(), 0.001);
        assertEquals(6.5, hoursDAO.findById(entry.getId()).orElseThrow().getHours(), 0.001);
    }

    @Test
    void updateHoursFailsOnceApproved() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);
        service.approve(entry.getId(), COORDINATOR);

        HoursException ex = assertThrows(HoursException.class,
                () -> service.updateHours(entry.getId(), VOLUNTEER, 12));

        assertTrue(ex.getMessage().toLowerCase().contains("already been reviewed"));
        assertEquals(4, hoursDAO.findById(entry.getId()).orElseThrow().getHours(), 0.001,
                "an approved figure must not be editable after the fact");
    }

    @Test
    void updateHoursFailsForSomeoneElsesClaim() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);

        HoursException ex = assertThrows(HoursException.class,
                () -> service.updateHours(entry.getId(), 999, 8));

        assertTrue(ex.getMessage().toLowerCase().contains("your own"));
    }

    // ---------- approving and rejecting ----------

    @Test
    void approveMarksTheClaimVerified() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);

        HoursEntry approved = service.approve(entry.getId(), COORDINATOR);

        assertEquals(HoursEntry.Status.APPROVED, approved.getStatus());
        assertTrue(approved.countsTowardsTotals());
    }

    @Test
    void rejectRecordsTheReason() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 12);

        HoursEntry rejected = service.reject(entry.getId(), COORDINATOR, "Event only ran for 4 hours");

        assertEquals(HoursEntry.Status.REJECTED, rejected.getStatus());
        assertEquals("Event only ran for 4 hours", rejected.getReviewNote());
        assertFalse(rejected.countsTowardsTotals());
    }

    @Test
    void rejectRequiresAReason() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);

        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, ""));
        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, "   "));
        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, null));
    }

    @Test
    void aCoordinatorCannotReviewHoursOnSomeoneElsesEvent() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);

        HoursException ex = assertThrows(HoursException.class,
                () -> service.approve(entry.getId(), OTHER_COORDINATOR));

        assertTrue(ex.getMessage().toLowerCase().contains("events you created"));
        assertTrue(hoursDAO.findById(entry.getId()).orElseThrow().isPending(),
                "the claim should be untouched after a rejected review attempt");
    }

    @Test
    void aClaimCannotBeReviewedTwice() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        HoursEntry entry = service.logHours(VOLUNTEER, event.getId(), 4);
        service.approve(entry.getId(), COORDINATOR);

        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, "changed my mind"));
    }

    // ---------- totals ----------

    @Test
    void onlyApprovedHoursCountTowardsTheCoordinatorTotal() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, 101);
        attended(event, 102);
        attended(event, 103);

        HoursEntry approved = service.logHours(101, event.getId(), 5);
        HoursEntry rejected = service.logHours(102, event.getId(), 8);
        service.logHours(103, event.getId(), 3);   // left pending

        service.approve(approved.getId(), COORDINATOR);
        service.reject(rejected.getId(), COORDINATOR, "Not verified");

        assertEquals(5.0, service.totalApprovedHoursForCoordinator(COORDINATOR), 0.001,
                "pending and rejected hours must be excluded from reported impact");
    }

    @Test
    void totalsAreScopedToTheCoordinatorsOwnEvents() throws HoursException {
        Event mine = pastEvent(COORDINATOR);
        Event theirs = pastEvent(OTHER_COORDINATOR);
        attended(mine, 101);
        attended(theirs, 102);

        HoursEntry a = service.logHours(101, mine.getId(), 5);
        HoursEntry b = service.logHours(102, theirs.getId(), 9);
        service.approve(a.getId(), COORDINATOR);
        service.approve(b.getId(), OTHER_COORDINATOR);

        assertEquals(5.0, service.totalApprovedHoursForCoordinator(COORDINATOR), 0.001);
        assertEquals(9.0, service.totalApprovedHoursForCoordinator(OTHER_COORDINATOR), 0.001);
    }

    @Test
    void pendingQueueOnlyShowsUnreviewedClaimsOnYourOwnEvents() throws HoursException {
        Event mine = pastEvent(COORDINATOR);
        Event theirs = pastEvent(OTHER_COORDINATOR);
        attended(mine, 101);
        attended(mine, 102);
        attended(theirs, 103);

        service.logHours(101, mine.getId(), 4);
        HoursEntry decided = service.logHours(102, mine.getId(), 4);
        service.logHours(103, theirs.getId(), 4);
        service.approve(decided.getId(), COORDINATOR);

        assertEquals(1, service.findPendingForCoordinator(COORDINATOR).size());
    }

    @Test
    void volunteerTotalCountsOnlyTheirOwnApprovedHours() throws HoursException {
        Event event = pastEvent(COORDINATOR);
        attended(event, VOLUNTEER);
        Event second = pastEvent(COORDINATOR);
        attended(second, VOLUNTEER);

        HoursEntry first = service.logHours(VOLUNTEER, event.getId(), 3);
        service.logHours(VOLUNTEER, second.getId(), 5);   // stays pending
        service.approve(first.getId(), COORDINATOR);

        assertEquals(3.0, service.totalApprovedHoursForVolunteer(VOLUNTEER), 0.001);
    }
}
