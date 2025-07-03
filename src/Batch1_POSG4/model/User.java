package Batch1_POSG4.model;

import java.time.LocalDateTime;

// Represents a user account with authentication and role information.
public class User {

    // Instance fields (public)

    // Instance fields (private)
    private long userId;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime createdAt;

    // Constructs a User with all fields specified.
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

    // Returns the user ID.
    public long getUserId() { return userId; }

    // Returns the username.
    public String getUsername() { return username; }

    // Returns the password hash.
    public String getPasswordHash() { return passwordHash; }

    // Returns the user's role.
    public String getRole() { return role; }

    // Returns the account creation timestamp.
    public LocalDateTime getCreatedAt() { return createdAt; }
}