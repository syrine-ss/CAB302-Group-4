package com.cab302.vic.model;

import java.util.Objects;

/**
 * Represents an application user, either a coordinator or a volunteer.
 * Passwords are never stored in plaintext, only as hashes.
 */
public class User {

    /**
     * User's role to be defined on sign up
     */
    public enum Role {
        /** User with co-ordinator role and privileges */
        COORDINATOR,
        /** User with volunteer role */
        VOLUNTEER }

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;

    /**
     * Creates a user with the specified details
     *
     * @param id the user's id
     * @param username the user's username
     * @param passwordHash the user's password hash
     * @param fullName the user's full name
     * @param email the user's email address
     * @param role the user's role
     */
    public User(int id, String username, String passwordHash,
                String fullName, String email, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    /**
     * Returns the user id.
     * @return the user id
     */
    public int getId() { return id; }
    /**
     * Returns user's username.
     * @return user's username
     */
    public String getUsername() { return username; }
    /**
     * Returns the user's password hash.
     * @return the user's password hash
     */
    public String getPasswordHash() { return passwordHash; }
    /**
     * Returns the user's full name.
     * @return the user's full name
     */
    public String getFullName() { return fullName; }
    /**
     * Returns the user's email.
     * @return the user's email
     */
    public String getEmail() { return email; }
    /**
     * Returns the user's role
     * @return the user's role
     */
    public Role getRole() { return role; }

    /**
     * Sets the user id
     * @param id user id
     */
    public void setId(int id) { this.id = id; }
    /**
     * Sets the user's full name
     * @param fullName user's full name
     */
    public void setFullName(String fullName) { this.fullName = fullName; }
    /**
     * Sets the user's email'
     * @param email user's email
     */
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
