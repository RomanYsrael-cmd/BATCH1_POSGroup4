package Batch1_POSG4.model;

import java.time.LocalDateTime;

public class User {
    private long userId;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime createdAt;

    /** DAO‐friendly constructor */
    public User(long userId,
                String username,
                String passwordHash,
                String role,
                LocalDateTime createdAt) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.createdAt    = createdAt;
    }

    // (plus your zero-arg or other constructors if you need them)

    // getters/setters...
    public long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    // … setters if you need them …
}
