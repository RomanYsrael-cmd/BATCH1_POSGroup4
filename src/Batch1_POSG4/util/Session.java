package Batch1_POSG4.util;

import Batch1_POSG4.model.User;

public class Session {
    private static final Session INSTANCE = new Session();

    private User currentUser;
    private long sessionId;    // ← added

    private Session() {}

    public static Session get() {
        return INSTANCE;
    }

    // -------- currentUser getters/setters --------
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // -------- sessionId getters/setters --------
    /**
     * The DB-generated session_id from tbl_LoginSession.
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * Set this right after you INSERT a new LoginSession.
     */
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
