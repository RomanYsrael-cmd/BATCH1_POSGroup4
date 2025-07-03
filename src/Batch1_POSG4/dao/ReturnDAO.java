package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Batch1_POSG4.model.ReturnModel;

// Provides database operations for processing returns, updating inventory, and retrieving return records.
public class ReturnDAO {

    // Private constants
    private static final String INSERT_RETURN_SQL =
        "INSERT INTO tbl_Return(sale_item_id, reason, refund_amount) VALUES (?, ?, ?)";

    private static final String SELECT_BY_ID_SQL =
        "SELECT return_id, sale_item_id, return_date, reason, refund_amount " +
        "FROM tbl_Return WHERE return_id = ?";

    private static final String SELECT_BY_SALE_SQL =
        "SELECT r.return_id, r.sale_item_id, r.return_date, r.reason, r.refund_amount " +
        "FROM tbl_Return r " +
        "JOIN tbl_SaleItem si ON r.sale_item_id = si.sale_item_id " +
        "WHERE si.sale_id = ? " +
        "ORDER BY r.return_date DESC";

    private static final String SELECT_SALEITEM_SQL =
        "SELECT product_id, quantity FROM tbl_SaleItem WHERE sale_item_id = ?";

    private static final String UPDATE_INVENTORY_SQL =
        "UPDATE tbl_Inventory SET quantity = quantity + ? WHERE product_id = ?";

    // Instance fields (private)
    private final String dbUrl;

    // Constructs a ReturnDAO with the specified database URL.
    public ReturnDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Processes a return, inserts a return record, and optionally restocks inventory.
    public long processReturn(long saleItemId,
                              String reason,
                              double refundAmount,
                              boolean restock) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            conn.setAutoCommit(false);

            long returnId;
            try (PreparedStatement ps = conn.prepareStatement(
                    INSERT_RETURN_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, saleItemId);
                ps.setString(2, reason);
                ps.setDouble(3, refundAmount);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Creating return failed, no rows affected.");
                }
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Creating return failed, no ID obtained.");
                    }
                    returnId = rs.getLong(1);
                }
            }

            if (restock) {
                long productId;
                int qty;
                // Fetch original sale quantity and product ID for restocking.
                try (PreparedStatement ps1 = conn.prepareStatement(SELECT_SALEITEM_SQL)) {
                    ps1.setLong(1, saleItemId);
                    try (ResultSet rs1 = ps1.executeQuery()) {
                        if (!rs1.next()) {
                            throw new SQLException("SaleItem not found: " + saleItemId);
                        }
                        productId = rs1.getLong("product_id");
                        qty       = rs1.getInt("quantity");
                    }
                }
                // Update inventory with returned quantity.
                try (PreparedStatement ps2 = conn.prepareStatement(UPDATE_INVENTORY_SQL)) {
                    ps2.setInt(1, qty);
                    ps2.setLong(2, productId);
                    ps2.executeUpdate();
                }
            }

            conn.commit();
            return returnId;
        }
    }

    // Retrieves a return record by its ID.
    public ReturnModel getReturnById(long returnId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, returnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new ReturnModel(
                    rs.getLong("return_id"),
                    rs.getLong("sale_item_id"),
                    rs.getTimestamp("return_date"),
                    rs.getString("reason"),
                    rs.getDouble("refund_amount")
                );
            }
        }
    }

    // Lists all returns for a given sale ID.
    public ObservableList<ReturnModel> listReturnsForSale(long saleId) throws SQLException {
        ObservableList<ReturnModel> results = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_SALE_SQL)) {
            ps.setLong(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new ReturnModel(
                        rs.getLong("return_id"),
                        rs.getLong("sale_item_id"),
                        rs.getTimestamp("return_date"),
                        rs.getString("reason"),
                        rs.getDouble("refund_amount")
                    ));
                }
            }
        }
        return results;
    }
}