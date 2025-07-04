package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Batch1_POSG4.model.User;

// Provides database operations for User entities, including CRUD.
public class UserDAO {

    // Instance fields (public)

    // Instance fields (private)
    private final String dbUrl;

    // Private constants
    private static final String INSERT_SQL =
        "INSERT INTO tbl_User(username, password_hash, role) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL =
        "SELECT user_id, username, password_hash, role, created_at FROM tbl_User ORDER BY user_id";
    private static final String UPDATE_SQL =
        "UPDATE tbl_User SET username = ?, role = ? WHERE user_id = ?";
    private static final String DELETE_SQL =
        "DELETE FROM tbl_User WHERE user_id = ?";

    // Constructs a UserDAO with the specified database URL.
    public UserDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Retrieves all users from the database and returns them as an ObservableList.
    public ObservableList<User> listAllUsers() throws SQLException {
        ObservableList<User> list = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id          = rs.getLong("user_id");
                String uname     = rs.getString("username");
                String passHash  = rs.getString("password_hash");
                String role      = rs.getString("role");
                Timestamp ts     = rs.getTimestamp("created_at");
                LocalDateTime created = ts.toLocalDateTime();
                list.add(new User(id, uname, passHash, role, created));
            }
        }
        return list;
    }

    // Adds a new user to the database.
    public void addUser(String username,
                        String passwordHash,
                        String role) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement pragma = conn.createStatement();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            pragma.execute("PRAGMA foreign_keys = ON");

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
        }
    }

    // Updates the username and role for a user with the specified ID.
    public void updateUser(long userId,
                           String username,
                           String role) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setLong(3, userId);
            ps.executeUpdate();
        }
    }

    // Deletes a user by their user ID.
    public void deleteUser(long userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}