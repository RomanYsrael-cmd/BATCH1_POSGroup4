package Batch1_POSG4.util;

import Batch1_POSG4.model.User;

// Manages the current user session and session ID for the application.
public class Session {

    // Private static fields
    private static final Session INSTANCE = new Session();

    // Instance fields (private)
    private User currentUser;
    private long sessionId;

    // Prevent external instantiation of Session.
    private Session() {}

    // Returns the singleton Session instance.
    public static Session get() {
        return INSTANCE;
    }

    // Returns the current user for this session.
    public User getCurrentUser() {
        return currentUser;
    }

    // Sets the current user for this session.
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Returns the DB-generated session_id from tbl_LoginSession.
    public long getSessionId() {
        return sessionId;
    }

    // Sets the session ID after inserting a new LoginSession.
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}