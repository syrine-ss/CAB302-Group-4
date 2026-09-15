package com.cab302.vic.model;

import java.util.Objects;

/**
 * Represents an application user, either a coordinator or a volunteer.
 * Passwords are never stored in plaintext, only as hashes.
 */
public class User {

    public enum Role { COORDINATOR, VOLUNTEER }

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;

    public User(int id, String username, String passwordHash,
                String fullName, String email, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    /** Set by the DAO once the row is inserted and a primary key is assigned. */
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
