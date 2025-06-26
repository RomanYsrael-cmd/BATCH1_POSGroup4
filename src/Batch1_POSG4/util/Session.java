// File: Batch1_POSG4/util/Session.java
package Batch1_POSG4.util;

import Batch1_POSG4.model.User;

public class Session {
    private static final Session INSTANCE = new Session();
    private User currentUser;

    private Session() {}

    public static Session get() {
        return INSTANCE;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
