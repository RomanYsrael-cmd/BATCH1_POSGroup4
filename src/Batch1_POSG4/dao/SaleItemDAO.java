// File: Batch1_POSG4/dao/SaleItemDao.java
package Batch1_POSG4.dao;

import Batch1_POSG4.view.SaleItemView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class SaleItemDAO {
    private final String dbUrl;

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

    public SaleItemDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Adds a line item to a sale. Returns the new sale_item_id.
     */
    public long addItemToSale(long saleId,
                              long productId,
                              int qty,
                              double unitPrice) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(INSERT_ITEM_SQL, Statement.RETURN_GENERATED_KEYS)) {
            //ps.execute("PRAGMA foreign_keys = ON");
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

    /**
     * Lists all line items for a given sale, ready for JavaFX binding.
     */
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

    /**
     * Updates the quantity (and recalculates line total) for an existing sale item.
     */
    public void updateSaleItem(long saleItemId, int newQty) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(UPDATE_ITEM_SQL)) {
            ps.setInt(1, newQty);
            ps.setInt(2, newQty);
            ps.setLong(3, saleItemId);
            ps.executeUpdate();
        }
    }

    /**
     * Removes a sale item from the sale.
     */
    public void removeItemFromSale(long saleItemId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(DELETE_ITEM_SQL)) {
            ps.setLong(1, saleItemId);
            ps.executeUpdate();
        }
    }
}
