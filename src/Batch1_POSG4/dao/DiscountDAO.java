package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Batch1_POSG4.model.Discount;

// Provides database operations for Discount entities, including CRUD and lookup by code.
public class DiscountDAO {

    // Instance fields (public)

    // Instance fields (private)
    private final String dbUrl;

    // Constructs a DiscountDAO with the specified database URL.
    public DiscountDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Retrieves all discounts from the database and returns them as a list.
    public List<Discount> fetchAll() throws SQLException {
        String sql = """
          SELECT d.discount_id,
                 d.discount_code,
                 d.description,
                 d.discount_type,
                 d.amount,
                 d.product_id,
                 p.name AS product_name
            FROM tbl_Discount d
       LEFT JOIN tbl_Product p ON d.product_id = p.product_id
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Discount> list = new ArrayList<>();
            while (rs.next()) {
                long   id   = rs.getLong("discount_id");
                String dc   = rs.getString("discount_code");
                String desc = rs.getString("description");
                String tp   = rs.getString("discount_type");
                double am   = rs.getDouble("amount");

                long   pid = rs.getLong("product_id");
                Long   productId   = rs.wasNull() ? null : pid;
                String pname       = rs.getString("product_name");

                list.add(new Discount(id, dc, desc, tp, am, productId, pname));
            }
            return list;
        }
    }

    // Inserts a new discount into the database.
    public void addDiscount(Discount d) throws SQLException {
        String sql = """
          INSERT INTO tbl_Discount
            (discount_code, description, discount_type, amount, product_id)
          VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, d.getDiscountCode());
            ps.setString(2, d.getDescription());
            ps.setString(3, d.getDiscountType());
            ps.setDouble(4, d.getAmount());

            if (d.getProductId() != null) {
                ps.setLong(5, d.getProductId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setDiscountId(keys.getLong(1));
            }
        }
    }

    // Deletes a discount by its code.
    public void deleteByCode(String code) throws SQLException {
        String sql = "DELETE FROM tbl_Discount WHERE discount_code = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    // Updates an existing discount identified by its original code.
    public void updateDiscount(Discount d, String originalCode) throws SQLException {
        String sql = """
            UPDATE tbl_Discount
               SET discount_code = ?, description = ?, discount_type = ?, amount = ?, product_id = ?
             WHERE discount_code = ?
        """;
        try (var conn = DriverManager.getConnection(dbUrl);
             var ps   = conn.prepareStatement(sql)) {
            ps.setString(1, d.getDiscountCode());
            ps.setString(2, d.getDescription());
            ps.setString(3, d.getDiscountType());
            ps.setDouble(4, d.getAmount());
            if (d.getProductId() != null) ps.setLong(5, d.getProductId());
            else                           ps.setNull (5, java.sql.Types.BIGINT);
            ps.setString(6, originalCode);
            ps.executeUpdate();
        }
    }

    // Deletes a discount by its primary key (discount ID).
    public void removeDiscount(long discountId) throws SQLException {
        String sql = "DELETE FROM tbl_Discount WHERE discount_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, discountId);
            ps.executeUpdate();
        }
    }

    // Finds a discount by its code and returns it as an Optional.
    public Optional<Discount> findByCode(String code) throws SQLException {
        String sql = """
          SELECT d.discount_id,
                 d.discount_code,
                 d.description,
                 d.discount_type,
                 d.amount,
                 d.product_id,
                 p.name AS product_name
            FROM tbl_Discount d
       LEFT JOIN tbl_Product p ON d.product_id = p.product_id
           WHERE d.discount_code = ?
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                long   id   = rs.getLong("discount_id");
                String dc   = rs.getString("discount_code");
                String desc = rs.getString("description");
                String tp   = rs.getString("discount_type");
                double am   = rs.getDouble("amount");

                long   pid = rs.getLong("product_id");
                Long   productId   = rs.wasNull() ? null : pid;
                String productName = rs.getString("product_name");

                return Optional.of(new Discount(
                    id, dc, desc, tp, am, productId, productName
                ));
            }
        }
    }
}