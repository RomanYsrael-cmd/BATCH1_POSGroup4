package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import Batch1_POSG4.model.Sale;

// Provides database operations for sales, including creation, lookup, update, and cancellation.
public class SaleDAO {

    // Instance fields (public)

    // Instance fields (private)
    private final String dbUrl;

    // Private constants
    private static final String INSERT_SALE_SQL =
        "INSERT INTO tbl_Sale(user_id, customer_id, sale_date, total_amount, payment_method) " +
        "VALUES (?, ?, CURRENT_TIMESTAMP, 0.0, ?)";

    private static final String SELECT_BY_ID_SQL =
        "SELECT s.sale_id, s.user_id, s.customer_id, s.sale_date, s.total_amount, s.payment_method, " +
        "c.name AS customer_name, u.username AS cashier " +
        "FROM tbl_Sale s " +
        "LEFT JOIN tbl_Customer c ON s.customer_id = c.customer_id " +
        "LEFT JOIN tbl_User u     ON s.user_id     = u.user_id " +
        "WHERE s.sale_id = ?";

    private static final String UPDATE_HEADER_SQL =
        "UPDATE tbl_Sale " +
        "SET customer_id = ?, payment_method = ?, total_amount = ? " +
        "WHERE sale_id = ?";

    private static final String DELETE_SALE_SQL =
        "DELETE FROM tbl_Sale WHERE sale_id = ?";

    // Constructs a SaleDAO with the specified database URL.
    public SaleDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Creates a new sale record and returns its generated ID.
    public long createSale(long userId, Integer customerId, String paymentMethod) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(INSERT_SALE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            if (customerId != null) ps.setInt(2, customerId);
            else                    ps.setNull(2, Types.INTEGER);
            ps.setString(3, paymentMethod);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creating sale failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                else throw new SQLException("Creating sale failed, no ID obtained.");
            }
        }
    }

    // Finds a sale by its ID and returns a Sale object, or null if not found.
    public Sale findById(long saleId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Sale(
                    rs.getLong("sale_id"),
                    rs.getLong("user_id"),
                    rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null,
                    rs.getTimestamp("sale_date"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_method"),
                    rs.getString("customer_name"),
                    rs.getString("cashier")
                );
            }
        }
    }

    // Updates the sale header with new customer, payment method, and total amount.
    public void updateSaleHeader(long saleId,
                                 Integer customerId,
                                 String paymentMethod,
                                 double totalAmount) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(UPDATE_HEADER_SQL)) {
            ps.setObject(1, customerId, Types.INTEGER);
            ps.setString(2, paymentMethod);
            ps.setDouble(3, totalAmount);
            ps.setLong(4, saleId);
            ps.executeUpdate();
        }
    }

    // Cancels (deletes) a sale by its ID.
    public void cancelSale(long saleId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(DELETE_SALE_SQL)) {
            ps.setLong(1, saleId);
            ps.executeUpdate();
        }
    }
}