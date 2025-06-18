package Batch1_POSG4.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:db/db_pos_g4.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}