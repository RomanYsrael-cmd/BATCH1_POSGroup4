package Batch1_POSG4.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import Batch1_POSG4.model.LoginSession;
import Batch1_POSG4.util.DBConnection;

public class LoginSessionDAO {

    /** Inserts a new tbl_LoginSession row and returns the generated session_id */
    public long create(LoginSession session) throws SQLException {
        String sql = "INSERT INTO tbl_LoginSession(user_id, ip_address, device_info) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, session.getUserId());
            ps.setString(2, session.getIpAddress());
            ps.setString(3, session.getDeviceInfo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                } else {
                    throw new SQLException("No session_id obtained.");
                }
            }
        }
    }

    /** Updates the logout_time to CURRENT_TIMESTAMP for the given session_id */
    public void closeSession(long sessionId) throws SQLException {
        String sql = "UPDATE tbl_LoginSession SET logout_time = CURRENT_TIMESTAMP WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.executeUpdate();
        }
    }
}
