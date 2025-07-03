package Batch1_POSG4.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Provides a static method to obtain a connection to the SQLite database.
public class DBConnection {

    // Private constants
    private static final String URL = "jdbc:sqlite:db/db_pos_g4.db";

    // Returns a new database connection using the configured URL.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}