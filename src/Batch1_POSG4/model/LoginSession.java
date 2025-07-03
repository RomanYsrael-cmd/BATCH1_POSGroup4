// src/main/java/Batch1_POSG4/model/LoginSession.java
package Batch1_POSG4.model;

import java.time.LocalDateTime;

public class LoginSession {
    private long sessionId;
    private long userId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
    private String deviceInfo;

    // full constructor (for loading from DB)
    public LoginSession(long sessionId, long userId, LocalDateTime loginTime,
                        LocalDateTime logoutTime, String ipAddress, String deviceInfo) {
        this.sessionId   = sessionId;
        this.userId      = userId;
        this.loginTime   = loginTime;
        this.logoutTime  = logoutTime;
        this.ipAddress   = ipAddress;
        this.deviceInfo  = deviceInfo;
    }

    // constructor for new record (no sessionId yet)
    public LoginSession(long userId, String ipAddress, String deviceInfo) {
        this.userId     = userId;
        this.ipAddress  = ipAddress;
        this.deviceInfo = deviceInfo;
    }

    // getters/setters...
    public long   getSessionId()   { return sessionId; }
    public void   setSessionId(long id) { this.sessionId = id; }
    public long   getUserId()      { return userId; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public LocalDateTime getLogoutTime() { return logoutTime; }
    public String getIpAddress()   { return ipAddress; }
    public String getDeviceInfo()  { return deviceInfo; }
}
