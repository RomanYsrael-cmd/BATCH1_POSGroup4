package Batch1_POSG4.dao;

//DAO for for shoing list of inventory
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Batch1_POSG4.view.ProductView;
import Batch1_POSG4.util.DBConnection;


public class ProductDAO {

    public ObservableList<ProductView> fetchInventoryWithCategory() {
        ObservableList<ProductView> list = FXCollections.observableArrayList();
        String sql = "SELECT p.product_id AS product_code, p.name AS product_name, " +
                     "c.name AS category, i.quantity, p.price, p.barcode " +
                     "FROM tbl_Product p " +
                     "JOIN tbl_Category c ON p.category_id = c.category_id " +
                     "JOIN tbl_Inventory i ON p.product_id = i.product_id";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
            ) {
            while (rs.next()) {
                list.add(new ProductView(
                    rs.getInt("product_code"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getString("barcode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or log properly
        }
        return list;
    }
}