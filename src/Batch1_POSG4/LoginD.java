//test not included to actual projecct
package Batch1_POSG4;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.SQLException;

public class LoginD {
    public static Connection connect(){
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:pos_data.db"; 
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database.");

        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }
}
