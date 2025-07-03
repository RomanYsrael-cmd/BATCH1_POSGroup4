// src/Batch1_POSG4/dao/DiscountDAO.java
package Batch1_POSG4.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Batch1_POSG4.model.Discount;

public class DiscountDAO {
    private final String dbUrl;

    public DiscountDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /** Fetch all discounts for a given sale */
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


    /** Insert a new discount */
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

    public void deleteByCode(String code) throws SQLException {
        String sql = "DELETE FROM tbl_Discount WHERE discount_code = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    /** Update an existing discount */
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


    /** Delete a discount by its primary key */
    public void removeDiscount(long discountId) throws SQLException {
        String sql = "DELETE FROM tbl_Discount WHERE discount_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, discountId);
            ps.executeUpdate();
        }
    }

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
