package com.cab302.vic.service;

import com.cab302.vic.dao.FakeEventDAO;
import com.cab302.vic.dao.FakeHoursDAO;
import com.cab302.vic.dao.FakeSignupDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour tests for {@link HoursService}, with the clock fixed to
 * 20 October 2026.
 */
class HoursServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
    private static final int COORDINATOR = 1;
    private static final int OTHER_COORDINATOR = 2;
    private static final int VOLUNTEER = 10;
    private static final int OTHER_VOLUNTEER = 11;

    private FakeHoursDAO hoursDAO;
    private FakeSignupDAO signupDAO;
    private FakeEventDAO eventDAO;
    private HoursService service;

    @BeforeEach
    void setUp() {
        hoursDAO = new FakeHoursDAO();
        signupDAO = new FakeSignupDAO();
        eventDAO = new FakeEventDAO();
        Clock fixed = Clock.fixed(TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        service = new HoursService(hoursDAO, signupDAO, eventDAO, fixed);
    }

    private Event pastEvent(int coordinatorId) {
        return eventDAO.create(Event.builder()
                .title("Working bee")
                .date(TODAY.minusDays(2).toString())
                .volunteersNeeded(5)
                .createdBy(coordinatorId)
                .build());
    }

    private Event attendedEvent(int volunteerId) {
        Event event = pastEvent(COORDINATOR);
        signupDAO.create(new Signup(0, event.getId(), volunteerId, true, TODAY.minusDays(5).toString()));
        return event;
    }

    // ---------- logging ----------

    @Test
    void logHoursCreatesPendingEntryDatedToday() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);

        HoursEntry entry = service.logHours(event.getId(), VOLUNTEER, 3.5);

        assertTrue(entry.getId() > 0);
        assertEquals(HoursEntry.Status.PENDING, entry.getStatus());
        assertEquals(3.5, entry.getHours(), 0.0001);
        assertEquals(TODAY.toString(), entry.getLoggedOn());
    }

    @Test
    void logHoursRejectsVolunteerNotMarkedAsAttending() {
        Event event = pastEvent(COORDINATOR);
        signupDAO.create(new Signup(0, event.getId(), VOLUNTEER, false, TODAY.toString()));

        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, 2));
        assertEquals(0, hoursDAO.size());
    }

    @Test
    void logHoursRejectsVolunteerWhoNeverSignedUp() {
        Event event = pastEvent(COORDINATOR);

        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, 2));
    }

    @Test
    void logHoursRejectsOutOfRangeHours() {
        Event event = attendedEvent(VOLUNTEER);

        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, 0));
        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, -1));
        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, 24.5));
        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, Double.NaN));
        assertEquals(0, hoursDAO.size());
    }

    @Test
    void logHoursAcceptsTheMaximum() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);

        service.logHours(event.getId(), VOLUNTEER, HoursService.MAX_HOURS);

        assertEquals(1, hoursDAO.size());
    }

    @Test
    void logHoursRejectsUnknownEvent() {
        assertThrows(HoursException.class, () -> service.logHours(999, VOLUNTEER, 2));
    }

    @Test
    void logHoursRejectsSecondEntryWhileFirstIsPendingOrApproved() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry first = service.logHours(event.getId(), VOLUNTEER, 2);

        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, 3));

        service.approve(first.getId(), COORDINATOR, null);
        assertThrows(HoursException.class, () -> service.logHours(event.getId(), VOLUNTEER, 3));
    }

    @Test
    void volunteerCanLogAgainAfterRejection() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry first = service.logHours(event.getId(), VOLUNTEER, 8);
        service.reject(first.getId(), COORDINATOR, "Event only ran for 3 hours");

        HoursEntry second = service.logHours(event.getId(), VOLUNTEER, 3);

        assertEquals(HoursEntry.Status.PENDING, second.getStatus());
        assertEquals(2, hoursDAO.size());
    }

    // ---------- reviewing ----------

    @Test
    void coordinatorCanApproveWithOptionalNote() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry entry = service.logHours(event.getId(), VOLUNTEER, 2);

        HoursEntry approved = service.approve(entry.getId(), COORDINATOR, "  thanks!  ");

        assertEquals(HoursEntry.Status.APPROVED, approved.getStatus());
        assertEquals("thanks!", approved.getReviewNote());
    }

    @Test
    void rejectRequiresAReason() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry entry = service.logHours(event.getId(), VOLUNTEER, 2);

        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, " "));
        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, null));
        assertTrue(hoursDAO.findById(entry.getId()).orElseThrow().isPending());
    }

    @Test
    void rejectRecordsTheReason() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry entry = service.logHours(event.getId(), VOLUNTEER, 2);

        HoursEntry rejected = service.reject(entry.getId(), COORDINATOR, "Wrong event");

        assertEquals(HoursEntry.Status.REJECTED, rejected.getStatus());
        assertEquals("Wrong event", rejected.getReviewNote());
    }

    @Test
    void onlyTheEventsCoordinatorCanReview() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry entry = service.logHours(event.getId(), VOLUNTEER, 2);

        assertThrows(HoursException.class,
                () -> service.approve(entry.getId(), OTHER_COORDINATOR, null));
        assertThrows(HoursException.class,
                () -> service.reject(entry.getId(), OTHER_COORDINATOR, "no"));
        assertTrue(hoursDAO.findById(entry.getId()).orElseThrow().isPending());
    }

    @Test
    void entryCannotBeReviewedTwice() throws HoursException {
        Event event = attendedEvent(VOLUNTEER);
        HoursEntry entry = service.logHours(event.getId(), VOLUNTEER, 2);
        service.approve(entry.getId(), COORDINATOR, null);

        assertThrows(HoursException.class, () -> service.reject(entry.getId(), COORDINATOR, "changed my mind"));
        assertEquals(HoursEntry.Status.APPROVED, hoursDAO.findById(entry.getId()).orElseThrow().getStatus());
    }

    @Test
    void reviewRejectsUnknownEntry() {
        assertThrows(HoursException.class, () -> service.approve(999, COORDINATOR, null));
    }

    // ---------- listing and totals ----------

    @Test
    void pendingForCoordinatorOnlyShowsTheirPendingEntries() throws HoursException {
        Event mine = attendedEvent(VOLUNTEER);
        signupDAO.create(new Signup(0, mine.getId(), OTHER_VOLUNTEER, true, TODAY.toString()));
        Event theirs = pastEvent(OTHER_COORDINATOR);
        signupDAO.create(new Signup(0, theirs.getId(), VOLUNTEER, true, TODAY.toString()));

        HoursEntry a = service.logHours(mine.getId(), VOLUNTEER, 2);
        HoursEntry b = service.logHours(mine.getId(), OTHER_VOLUNTEER, 3);
        service.logHours(theirs.getId(), VOLUNTEER, 4);
        service.approve(a.getId(), COORDINATOR, null);

        assertEquals(1, service.pendingForCoordinator(COORDINATOR).size());
        assertEquals(b.getId(), service.pendingForCoordinator(COORDINATOR).get(0).getId());
        assertEquals(1, service.pendingForCoordinator(OTHER_COORDINATOR).size());
    }

    @Test
    void totalCountsOnlyApprovedHours() throws HoursException {
        Event first = attendedEvent(VOLUNTEER);
        Event second = attendedEvent(VOLUNTEER);
        Event third = attendedEvent(VOLUNTEER);
        service.approve(service.logHours(first.getId(), VOLUNTEER, 2.5).getId(), COORDINATOR, null);
        service.reject(service.logHours(second.getId(), VOLUNTEER, 4).getId(), COORDINATOR, "No");
        service.logHours(third.getId(), VOLUNTEER, 6);

        assertEquals(2.5, service.totalApprovedHours(VOLUNTEER), 0.0001);
        assertEquals(3, service.entriesForVolunteer(VOLUNTEER).size());
        assertEquals(0.0, service.totalApprovedHours(OTHER_VOLUNTEER), 0.0001);
    }
}
