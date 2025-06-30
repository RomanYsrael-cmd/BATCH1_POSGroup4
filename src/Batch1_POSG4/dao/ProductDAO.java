package Batch1_POSG4.dao;

import Batch1_POSG4.util.DBConnection;
import Batch1_POSG4.view.ProductView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ProductDAO {

    public ObservableList<ProductView> fetchInventoryWithCategory() {
        ObservableList<ProductView> list = FXCollections.observableArrayList();
        String sql = """
            SELECT p.product_id    AS product_code,
                   p.name          AS product_name,
                   c.name          AS category,
                   i.quantity      AS quantity,
                   p.price         AS price,
                   p.barcode       AS barcode
              FROM tbl_Product p
              JOIN tbl_Category c   ON p.category_id = c.category_id
              JOIN tbl_Inventory i  ON p.product_id = i.product_id
             ORDER BY p.name
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new ProductView(
                    rs.getInt   ("product_code"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt   ("quantity"),
                    rs.getDouble("price"),
                    rs.getString("barcode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<Category> fetchAllCategories() {
        ObservableList<Category> list = FXCollections.observableArrayList();
        String sql = "SELECT category_id, name FROM tbl_Category ORDER BY name";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<ProductView> search(
            String filterTerm,
            String searchBy,
            Integer categoryId) throws SQLException
    {
        ObservableList<ProductView> list = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("""
            SELECT p.product_id    AS product_code,
                   p.name          AS product_name,
                   c.name          AS category,
                   i.quantity      AS quantity,
                   p.price         AS price,
                   p.barcode       AS barcode
              FROM tbl_Product p
              JOIN tbl_Category c   ON p.category_id = c.category_id
              JOIN tbl_Inventory i  ON p.product_id = i.product_id
             WHERE 1=1
        """);

        if (categoryId != null) {
            sql.append(" AND p.category_id = ? ");
        }

        boolean hasText = filterTerm != null && !filterTerm.isBlank();
        if (hasText) {
            String col = searchBy.equalsIgnoreCase("Barcode")
                       ? "p.barcode"
                       : "p.name";
            sql.append(" AND lower(").append(col).append(") LIKE ? ");
        }

        sql.append(" ORDER BY p.name");

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            int idx = 1;

            if (categoryId != null) {
                ps.setInt(idx++, categoryId);
            }
            if (hasText) {
                ps.setString(idx++, "%" + filterTerm.toLowerCase() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductView(
                        rs.getInt   ("product_code"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt   ("quantity"),
                        rs.getDouble("price"),
                        rs.getString("barcode")
                    ));
                }
            }
        }

        return list;
    }

    public static class Category {
        private final Integer categoryId;
        private final String  name;
        public Category(Integer categoryId, String name) {
            this.categoryId = categoryId;
            this.name       = name;
        }
        public Integer getCategoryId() { return categoryId; }
        public String  getName()       { return name; }
        @Override
        public String toString() { return name; }
    }
}
