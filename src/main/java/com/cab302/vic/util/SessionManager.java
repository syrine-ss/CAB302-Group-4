package com.cab302.vic.util;

import com.cab302.vic.model.User;

/**
 * Tracks which user is currently signed in.
 * A simple in-memory singleton, sufficient for a desktop app with a single
 * user session at a time.
 */
public final class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    /**
     * Returns the instance of the session manager, creating it if needed
     * @return the session manager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set current user
     * @param user the user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Get current user
     * @return current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is logged in
     * @return true if current user is not null
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if a logged-in user has co-ordinator role
     * @return true if current user if co-ordinator
     */
    public boolean isCoordinator() {
        return isLoggedIn() && currentUser.getRole() == User.Role.COORDINATOR;
    }

    /**
     * Set current user to null
     */
    public void clear() {
        this.currentUser = null;
    }
}
