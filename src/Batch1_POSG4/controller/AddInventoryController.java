package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.util.Session;

// Handles adding new inventory items to the database and manages the add inventory dialog.
public class AddInventoryController {

    // Instance fields (public)

    // Instance fields (private)
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtPrice;
    @FXML private TextField txtProductCode;
    @FXML private TextField txtProductDescription;
    @FXML private TextField txtProductName;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtLocation;

    long currentUserId = Session.get().getCurrentUser().getUserId();

    // Establishes a connection to the SQLite database.
    private Connection connect() {
        String url = "jdbc:sqlite:db/db_pos_g4.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // Loads all categories from the database and populates the category combo box.
    public void loadCategories() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT name FROM tbl_Category";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
            cmbCategory.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles the add button, validates input, registers a new product, and shows confirmation or error.
    @FXML
    private void handlesAdd(ActionEvent event) {
        String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
        InventoryAddDAO svc = new InventoryAddDAO(dbUrl);

        String pName = txtProductName.getText();
        String pDescription = txtProductDescription.getText();
        Double pPrice = Double.parseDouble(txtPrice.getText());

        // Get the selected category index and convert to category ID (assuming 1-based IDs).
        Integer pCategoryID = cmbCategory.getSelectionModel().getSelectedIndex() + 1;
        System.out.println(pCategoryID);

        String pBarcode = txtProductCode.getText();
        Integer pQuantity = Integer.parseInt(txtQuantity.getText());
        String pLocation = txtLocation.getText();

        try {
            long prodId = svc.registerNewProduct(pName, pDescription, pPrice, pCategoryID, pBarcode, pQuantity, pLocation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product Registered");
            alert.setHeaderText(null);
            alert.setContentText("New product registered with ID: " + prodId);
            alert.showAndWait();

            txtProductName.clear();
            txtProductDescription.clear();
            txtPrice.clear();
            txtProductCode.clear();
            txtQuantity.clear();
            txtLocation.clear();
            cmbCategory.getSelectionModel().clearSelection();

        } catch (SQLException ex) {
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to register product");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    // Handles the cancel button, closing the add inventory dialog.
    @FXML
    private void handlesCancel(ActionEvent event) throws IOException {
        Stage dlg = (Stage)((Node)event.getSource()).getScene().getWindow();
        dlg.close();
    }

    // Handles the clear button, clearing all input fields in the dialog.
    @FXML
    private void handlesClear(ActionEvent event) {
        txtProductName.clear();
        txtProductDescription.clear();
        txtPrice.clear();
        txtProductCode.clear();
        txtQuantity.clear();
        txtLocation.clear();
        cmbCategory.getSelectionModel().clearSelection();
    }
}