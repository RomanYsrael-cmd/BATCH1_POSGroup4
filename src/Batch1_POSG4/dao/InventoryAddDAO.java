package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Handles inserting new products and managing inventory levels in the database.
public class InventoryAddDAO {

    // Private constants
    private static final String INSERT_PRODUCT_SQL =
        "INSERT INTO tbl_Product(name, description, price, stock_quantity, category_id, barcode) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_INVENTORY_SQL =
        "INSERT INTO tbl_Inventory(product_id, quantity, location) VALUES (?, ?, ?)";

    private static final String UPDATE_INVENTORY_SQL =
        "UPDATE tbl_Inventory SET quantity = quantity + ? WHERE product_id = ?";

    private static final String SET_INVENTORY_SQL =
        "UPDATE tbl_Inventory SET quantity = ? WHERE product_id = ?";

    private static final String DELETE_INVENTORY_SQL =
        "DELETE FROM tbl_Inventory WHERE product_id = ?";

    // Instance fields (private)
    private final String dbUrl;

    // Constructs the DAO with the given database URL.
    public InventoryAddDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Registers a new product and its initial inventory, returning the new product's ID.
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

    // Deletes an inventory record for a product by product ID.
    public void deleteInventory(long productId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            try (PreparedStatement ps = conn.prepareStatement(DELETE_INVENTORY_SQL)) {
                ps.setLong(1, productId);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No inventory record found for product_id=" + productId);
                }
            }
        }
    }

    // Adjusts inventory by adding delta to the quantity for the given product ID.
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

    // Adjusts inventory using an existing connection, for transactional updates.
    public void adjustInventory(Connection conn, long productId, int delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_INVENTORY_SQL)) {
            ps.setInt(1, delta);
            ps.setLong(2, productId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No inventory record found for product_id=" + productId);
            }
        }
    }

    // Inserts a new product into the database and returns its generated ID.
    private long insertProduct(Connection conn, String name, String description, double price, int quantity, int categoryId, String barcode) throws SQLException {
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

    // Inserts an inventory record for the given product.
    private void insertInventory(Connection conn, long productId, int quantity, String location) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_INVENTORY_SQL)) {
            ps.setLong(1, productId);
            ps.setInt(2, quantity);
            ps.setString(3, location);
            ps.executeUpdate();
        }
    }

    // Sets the inventory quantity for a product.
    public void setInventory(long productId, int quantity) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            try (PreparedStatement ps = conn.prepareStatement(SET_INVENTORY_SQL)) {
                ps.setInt(1, quantity);
                ps.setLong(2, productId);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No inventory record found for product_id=" + productId);
                }
            }
        }
    }
}