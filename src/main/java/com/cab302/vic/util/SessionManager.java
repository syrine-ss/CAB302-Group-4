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

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isCoordinator() {
        return isLoggedIn() && currentUser.getRole() == User.Role.COORDINATOR;
    }

    public void clear() {
        this.currentUser = null;
    }
}
