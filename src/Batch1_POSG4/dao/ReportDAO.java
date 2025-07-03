package Batch1_POSG4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Provides reporting operations for sales data, including available periods and totals per period.
public class ReportDAO {

    // Private constants
    private static final DateTimeFormatter UI_FMT  = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter SQL_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    // Instance fields (private)
    private final String dbUrl;

    // Constructs a ReportDAO with the specified database URL.
    public ReportDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Retrieves all available sales periods as formatted strings for the UI.
    public ObservableList<String> fetchAvailablePeriods() throws SQLException {
        String sql = """
            SELECT DISTINCT strftime('%Y-%m', sale_date) AS ym
              FROM tbl_Sale
             ORDER BY ym
            """;

        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            ObservableList<String> periods = FXCollections.observableArrayList();
            while (rs.next()) {
                YearMonth ym = YearMonth.parse(rs.getString("ym"), SQL_FMT);
                periods.add(ym.format(UI_FMT));
            }
            return periods;
        }
    }

    // Returns the total sales amount for the given UI period string.
    public double fetchTotalForPeriod(String uiPeriod) throws SQLException {
        YearMonth ym = YearMonth.parse(uiPeriod, UI_FMT);
        String ymSql = ym.format(SQL_FMT);

        String sql = """
            SELECT SUM(total_amount) AS sum_total
              FROM tbl_Sale
             WHERE strftime('%Y-%m', sale_date) = ?
            """;

        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, ymSql);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("sum_total") : 0.0;
            }
        }
    }
}