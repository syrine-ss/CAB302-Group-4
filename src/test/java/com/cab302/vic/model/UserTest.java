package com.cab302.vic.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorSetsAllFields() {
        User user = new User(1, "sam", "hashedpw", "Sam Turner",
                "sam@example.com", User.Role.VOLUNTEER);

        assertEquals(1, user.getId());
        assertEquals("sam", user.getUsername());
        assertEquals("hashedpw", user.getPasswordHash());
        assertEquals("Sam Turner", user.getFullName());
        assertEquals("sam@example.com", user.getEmail());
        assertEquals(User.Role.VOLUNTEER, user.getRole());
    }

    @Test
    void settersUpdateFields() {
        User user = new User(2, "maia", "hashedpw", "Maia Sherwin",
                "maia@example.com", User.Role.COORDINATOR);

        user.setFullName("Maia S.");
        user.setEmail("maia.s@example.com");
        user.setId(99);

        assertEquals("Maia S.", user.getFullName());
        assertEquals("maia.s@example.com", user.getEmail());
        assertEquals(99, user.getId());
    }

    @Test
    void equalityBasedOnIdAndUsername() {
        User a = new User(1, "sam", "h", "Sam T", "s@e.com", User.Role.VOLUNTEER);
        User b = new User(1, "sam", "different", "Different Name", "diff@e.com", User.Role.COORDINATOR);
        User c = new User(2, "sam", "h", "Sam T", "s@e.com", User.Role.VOLUNTEER);

        assertEquals(a, b, "Same id and username should be equal");
        assertNotEquals(a, c, "Different id should not be equal");
    }
}
