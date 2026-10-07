package com.cab302.vic.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignupTest {

    @Test
    void constructorSetsAllFields() {
        Signup s = new Signup(3, 10, 20, false, "2026-10-07");

        assertEquals(3, s.getId());
        assertEquals(10, s.getEventId());
        assertEquals(20, s.getUserId());
        assertFalse(s.isAttended());
        assertEquals("2026-10-07", s.getSignedUpOn());
    }

    @Test
    void attendanceCanBeMarkedAndCleared() {
        Signup s = new Signup(1, 10, 20, false, "2026-10-07");

        s.setAttended(true);
        assertTrue(s.isAttended());

        s.setAttended(false);
        assertFalse(s.isAttended());
    }

    @Test
    void equalityUsesIdEventAndVolunteer() {
        Signup a = new Signup(1, 10, 20, false, "2026-10-07");
        Signup sameRowLaterState = new Signup(1, 10, 20, true, "2026-10-08");
        Signup otherVolunteer = new Signup(1, 10, 21, false, "2026-10-07");

        assertEquals(a, sameRowLaterState);
        assertEquals(a.hashCode(), sameRowLaterState.hashCode());
        assertNotEquals(a, otherVolunteer);
    }
}
