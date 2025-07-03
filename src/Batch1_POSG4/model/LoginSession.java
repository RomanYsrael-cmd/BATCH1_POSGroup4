package Batch1_POSG4.model;

import java.time.LocalDateTime;

// Represents a user login session, including login/logout times and device info.
public class LoginSession {

    // Instance fields (public)

    // Instance fields (private)
    private long sessionId;
    private long userId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
    private String deviceInfo;

    // Constructs a LoginSession with all fields (for loading from DB).
    public LoginSession(long sessionId, long userId, LocalDateTime loginTime,
                        LocalDateTime logoutTime, String ipAddress, String deviceInfo) {
        this.sessionId   = sessionId;
        this.userId      = userId;
        this.loginTime   = loginTime;
        this.logoutTime  = logoutTime;
        this.ipAddress   = ipAddress;
        this.deviceInfo  = deviceInfo;
    }

    // Constructs a LoginSession for a new record (no sessionId yet).
    public LoginSession(long userId, String ipAddress, String deviceInfo) {
        this.userId     = userId;
        this.ipAddress  = ipAddress;
        this.deviceInfo = deviceInfo;
    }

    // Returns the session ID.
    public long getSessionId() { return sessionId; }

    // Sets the session ID.
    public void setSessionId(long id) { this.sessionId = id; }

    // Returns the user ID.
    public long getUserId() { return userId; }

    // Returns the login time.
    public LocalDateTime getLoginTime() { return loginTime; }

    // Returns the logout time.
    public LocalDateTime getLogoutTime() { return logoutTime; }

    // Returns the IP address for the session.
    public String getIpAddress() { return ipAddress; }

    // Returns the device information for the session.
    public String getDeviceInfo() { return deviceInfo; }
}