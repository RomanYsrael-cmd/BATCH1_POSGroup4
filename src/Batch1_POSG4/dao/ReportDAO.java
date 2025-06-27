package Batch1_POSG4.dao;

import Batch1_POSG4.model.MonthlyReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class ReportDAO {
    private final String dbUrl;
    private static final DateTimeFormatter UI_FMT = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter SQL_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    public ReportDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Returns a list of all month-year strings (e.g. "June 2025")
     * for which tbl_Sale has at least one sale_date.
     */
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

    /**
     * For a single UI‐format period like "June 2025",
     * compute the total sales in that month.
     */
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
