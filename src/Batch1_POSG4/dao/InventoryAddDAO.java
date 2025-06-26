// File: Batch1_POSG4/dao/InventoryAddDAO.java
package Batch1_POSG4.dao;

import java.sql.*;

/**
 * DAO for inserting new products and managing inventory levels.
 */
public class InventoryAddDAO {
    private static final String INSERT_PRODUCT_SQL =
        "INSERT INTO tbl_Product(name, description, price, stock_quantity, category_id, barcode) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_INVENTORY_SQL =
        "INSERT INTO tbl_Inventory(product_id, quantity, location) VALUES (?, ?, ?)";

    private static final String UPDATE_INVENTORY_SQL =
        "UPDATE tbl_Inventory SET quantity = quantity + ? WHERE product_id = ?";

    private final String dbUrl;

    public InventoryAddDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Registers a new product and seeds its initial inventory.
     * @return the generated product_id
     */
    public long registerNewProduct(
            String name,
            String description,
            double price,
            int categoryId,
            String barcode,
            int initialQuantity,
            String location
    ) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            conn.setAutoCommit(false);

            long productId = insertProduct(conn, name, description, price, initialQuantity, categoryId, barcode);
            insertInventory(conn, productId, initialQuantity, location);

            conn.commit();
            return productId;
        }
    }

    /**
     * Adjusts inventory using its own connection (not recommended for multi-step transactions).
     * @param productId the product to update
     * @param delta     positive to add, negative to subtract
     */
    public void adjustInventory(long productId, int delta) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_INVENTORY_SQL)) {
                ps.setInt(1, delta);
                ps.setLong(2, productId);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No inventory record found for product_id=" + productId);
                }
            }
        }
    }

    /**
     * Adjusts inventory using an existing connection (for use in batch transactions).
     * @param conn      an open connection with autoCommit disabled
     * @param productId the product to update
     * @param delta     positive to add, negative to subtract
     */
    public void adjustInventory(Connection conn, long productId, int delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_INVENTORY_SQL)) {
            ps.setInt(1, delta);
            ps.setLong(2, productId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No inventory record found for product_id=" + productId);
            }
        }
    }

    // … insertProduct and insertInventory unchanged …
    private long insertProduct(
        Connection conn,
        String name,
        String description,
        double price,
        int quantity,
        int categoryId,
        String barcode
    ) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_PRODUCT_SQL,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setInt(5, categoryId);
            ps.setString(6, barcode);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Failed to insert product.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                else throw new SQLException("No product ID obtained.");
            }
        }
    }

    private void insertInventory(
        Connection conn,
        long productId,
        int quantity,
        String location
    ) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_INVENTORY_SQL)) {
            ps.setLong(1, productId);
            ps.setInt(2, quantity);
            ps.setString(3, location);
            ps.executeUpdate();
        }
    }
}
