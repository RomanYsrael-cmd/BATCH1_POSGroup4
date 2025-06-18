package Batch1_POSG4.dao;
// Declares this class is part of the Batch1_POSG4.dao package

import java.sql.*;
// Imports JDBC classes (Connection, DriverManager, SQLException, PreparedStatement, ResultSet, Statement)

public class InventoryAddDAO {
    
    // SQL template for inserting a new product into tbl_Product
    private static final String INSERT_PRODUCT_SQL = "INSERT INTO tbl_Product(name, description, price, stock_quantity, category_id, barcode) VALUES (?, ?, ?, ?, ?, ?)";
    // SQL template for inserting an inventory record into tbl_Inventory
    private static final String INSERT_INVENTORY_SQL = "INSERT INTO tbl_Inventory(product_id, quantity, location) VALUES (?, ?, ?)";
    // * Note kaya dalawa to, kasi 2 table sa database yung need mabago. SO dalawang sql statement template yung nandito.
    // Kung isang database lang naman, isang sql statement template lang ang need
    // Same sa method, dalawang method yung nandito, kasi dalawang sql statement yung gagamitin
    // For detailed sql statement turorial, hanap na lang kayo sa net, but i recomment this: https://www.w3schools.com/sql/default.asp
    // Puro insert yung nandito, check niyo kung pano yung "Select", "Delete" or "Update"
    // Wag mahiyang gamitin si chatgpt kung di magets HAHAHAHAHAHA

    // Yung database table natin, nasa db/db_pos_g4.db
    // Download kayo ng sqllite viewer: https://sqlitebrowser.org/
    

    
    // JDBC URL of the SQLite database file (e.g. "jdbc:sqlite:path/to/db")
    // JDBC is an extension used to connect to database sql database
    // ALWAYS ADD
    private final String dbUrl;

    // Constructor that saves the database URL for later connections
    // ALWAYS ADD
    public InventoryAddDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    
    
    // Begins the process of registering a new product and seeding its inventory
    // Depends kung anong klaseng DAO yung need gawin.
    // In this case, registering a new product
    public long registerNewProduct(String name, String description, double price, int categoryId, String barcode, int initialQuantity, String location) throws SQLException {
        
        // Opens a connection to the SQLite database
        Connection conn = DriverManager.getConnection(dbUrl);


        try {
            // Wag kalimutan dahil merong foreign key
            // Enables foreign-key constraints in SQLite for this connection
            // ALWAYS ADD
            conn.createStatement().execute("PRAGMA foreign_keys = ON");

            // Turns off auto-commit to manage the two inserts in one transaction
            // ALWAYS ADD
            conn.setAutoCommit(false);


            // Calls helper to insert into tbl_Product and returns the new product_id
            long productId = insertProduct(conn, name, description, price, initialQuantity ,categoryId, barcode);
            
            // Calls helper to insert a corresponding row in tbl_Inventory
            insertInventory(conn, productId, initialQuantity, location);
            
            // Commits the transaction so both inserts are saved atomically
            conn.commit();
 
            // Returns the newly generated product_id to the caller
            return productId;
        } catch (SQLException e) {
            conn.rollback();
            // Rolls back both inserts if any error occurs
            throw e;
            // Propagates the exception


        } finally {
            // Ensures the database connection is always closed after being used
            conn.close();
        }
    }

    // Helper method to insert a new product and fetch its generated ID    
    private long insertProduct(Connection conn, String name, String description, double price, int quantity, int categoryId,String barcode) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_PRODUCT_SQL, 
        // Prepares the SQL, requesting to return auto-generated keys
        Statement.RETURN_GENERATED_KEYS)) {
            
            
            //private static final String INSERT_PRODUCT_SQL = "INSERT INTO tbl_Product(name, description, price, stock_quantity, category_id, barcode) VALUES (?, ?, ?, ?, ?, ?)";
            // yung parameter is kung pangilan siya sa sql statement
            // STARTING SA 1 hindi sa 0

            // Binds product name to parameter 1
            ps.setString(1, name);
            // Binds product name to parameter 2
            ps.setString(2, description);
            // Binds product name to parameter 3
            ps.setDouble(3, price);
            // Binds product name to parameter 4
            ps.setInt(4, quantity);
            // Binds product name to parameter 5
            ps.setInt(5, categoryId);
            // Binds product name to parameter 6
            ps.setString(6, barcode);

            // Executes the INSERT; if no row was inserted, throws an error
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Failed to insert product.");
            }

            // Retrieves the generated keys ResultSet
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("No product ID obtained.");
                }
                // Returns the first generated key (product_id), or errors if missing
            }
        }
    }
    private void insertInventory(Connection conn,long productId,int quantity,String location) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_INVENTORY_SQL)) {
            //Same logic as above
            ps.setLong(1, productId);
            ps.setInt(2, quantity);
            ps.setString(3, location);
            ps.executeUpdate();
        }
    }
}