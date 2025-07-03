package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Batch1_POSG4.view.SaleItemView;

// Provides database operations for sale items, including add, list, update, and remove.
public class SaleItemDAO {

    // Instance fields (public)

    // Instance fields (private)
    private final String dbUrl;

    // Private constants
    private static final String INSERT_ITEM_SQL =
        "INSERT INTO tbl_SaleItem(sale_id, product_id, quantity, unit_price, total_price) " +
        "VALUES (?, ?, ?, ?, ? * ?)";

    private static final String SELECT_ITEMS_SQL =
        "SELECT si.sale_item_id, si.sale_id, si.product_id, p.name AS productName, " +
        "si.quantity, si.unit_price, (si.quantity * si.unit_price) AS totalPrice " +
        "FROM tbl_SaleItem si " +
        "JOIN tbl_Product p ON si.product_id = p.product_id " +
        "WHERE si.sale_id = ?";

    private static final String UPDATE_ITEM_SQL =
        "UPDATE tbl_SaleItem " +
        "SET quantity = ?, total_price = ? * unit_price " +
        "WHERE sale_item_id = ?";

    private static final String DELETE_ITEM_SQL =
        "DELETE FROM tbl_SaleItem WHERE sale_item_id = ?";

    // Constructs a SaleItemDAO with the specified database URL.
    public SaleItemDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Adds a new item to a sale and returns the generated sale_item_id.
    public long addItemToSale(long saleId,
                              long productId,
                              int qty,
                              double unitPrice) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(INSERT_ITEM_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, saleId);
            ps.setLong(2, productId);
            ps.setInt(3, qty);
            ps.setDouble(4, unitPrice);
            ps.setInt(5, qty);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Adding item to sale failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                else throw new SQLException("Adding item failed, no ID obtained.");
            }
        }
    }

    // Lists all sale items for a given sale ID.
    public ObservableList<SaleItemView> listItemsForSale(long saleId) throws SQLException {
        ObservableList<SaleItemView> list = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(SELECT_ITEMS_SQL)) {
            ps.setLong(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SaleItemView(
                        rs.getLong("sale_item_id"),
                        rs.getLong("sale_id"),
                        rs.getLong("product_id"),
                        rs.getString("productName"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("totalPrice")
                    ));
                }
            }
        }
        return list;
    }

    // Updates the quantity and total price for a sale item.
    public void updateSaleItem(long saleItemId, int newQty) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(UPDATE_ITEM_SQL)) {
            ps.setInt(1, newQty);
            ps.setInt(2, newQty);
            ps.setLong(3, saleItemId);
            ps.executeUpdate();
        }
    }

    // Removes a sale item from a sale by its sale_item_id.
    public void removeItemFromSale(long saleItemId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(DELETE_ITEM_SQL)) {
            ps.setLong(1, saleItemId);
            ps.executeUpdate();
        }
    }
}