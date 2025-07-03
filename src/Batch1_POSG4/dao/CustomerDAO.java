package Batch1_POSG4.dao;

import Batch1_POSG4.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CustomerDAO {
    private final String dbUrl;

    public CustomerDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public ObservableList<Customer> fetchAll() throws SQLException {
        String sql = """
            SELECT customer_id,
                   name,
                   email,
                   phone,
                   loyalty_points
              FROM tbl_Customer
            """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            ObservableList<Customer> list = FXCollections.observableArrayList();
            while (rs.next()) {
                list.add(new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("loyalty_points")
                ));
            }
            return list;
        }
    }

    /**
     * Inserts a new customer row and returns a Customer with its generated ID.
     * loyalty_points will default to 0.
     */
    public Customer create(String name, String email, String phone) throws SQLException {
        String sql = """
            INSERT INTO tbl_Customer (name, email, phone)
            VALUES (?, ?, ?)
            """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    // loyalty_points defaults to 0 on creation
                    return new Customer(newId, name, email, phone, 0);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    public void updateLoyaltyPoints(int customerId, int points) throws SQLException {
        String sql = """
            UPDATE tbl_Customer
               SET loyalty_points = ?
             WHERE customer_id = ?
            """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setInt(2, customerId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No customer found with ID " + customerId);
            }
        }
    }
    
    public int addLoyaltyPoints(int customerId, int delta) throws SQLException {
        String sql = """
            UPDATE tbl_Customer
               SET loyalty_points = loyalty_points + ?
             WHERE customer_id = ?
            """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, customerId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No customer found with ID " + customerId);
            }
        }

        // fetch the updated total
        String query = "SELECT loyalty_points FROM tbl_Customer WHERE customer_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("loyalty_points");
                }
                throw new SQLException("Failed to fetch updated loyalty points");
            }
        }
    }

}
