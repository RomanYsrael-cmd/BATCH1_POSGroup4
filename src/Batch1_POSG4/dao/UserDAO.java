package Batch1_POSG4.dao;

import Batch1_POSG4.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserDAO {
    private final String dbUrl;
    public UserDAO(String dbUrl) { this.dbUrl = dbUrl; }

    public ObservableList<User> listAllUsers() throws SQLException {
        var list = FXCollections.<User>observableArrayList();
        String sql = """
            SELECT user_id, username, role, created_at
              FROM tbl_User
           ORDER BY user_id
        """;
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next()) {
                list.add(new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    null,               // don’t expose hash
                    rs.getString("role"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return list;
    }

    public void addUser(String username,
                        String passwordHash,
                        String role) throws SQLException
    {
        String sql = """
            INSERT INTO tbl_User(username, password_hash, role, created_at)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
        """;
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            if (ps.executeUpdate() == 0)
                throw new SQLException("Insert failed, no rows affected.");
        }
    }
}
