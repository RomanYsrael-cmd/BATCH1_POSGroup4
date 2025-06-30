package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.Node;

import javafx.stage.Stage;

public class AddInventoryController {
    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtProductCode;

    @FXML
    private TextField txtProductDescription;

    @FXML
    private TextField txtProductName;

    @FXML
    private TextField txtQuantity;
    
    @FXML
    private TextField txtLocation;
    
    long currentUserId = Session.get().getCurrentUser().getUserId();

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

    @FXML
    private void handlesAdd(ActionEvent event) {
        // Setup DB connection
        String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
        InventoryAddDAO svc = new InventoryAddDAO(dbUrl);

        // Get input values
        String pName = txtProductName.getText();
        String pDescription = txtProductDescription.getText();
        Double pPrice = Double.parseDouble(txtPrice.getText());

        //???
        Integer pCategoryID = cmbCategory.getSelectionModel().getSelectedIndex() + 1;
        System.out.println(pCategoryID);
        //??

        String pBarcode = txtProductCode.getText();
        Integer pQuantity = Integer.parseInt(txtQuantity.getText());
        
        String pLocation = txtLocation.getText();

        try {
            long prodId = svc.registerNewProduct(pName, pDescription, pPrice, pCategoryID, pBarcode, pQuantity, pLocation);
            //System.out.println("New product registered with ID: " + prodId);

            //Show confirmation alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product Registered");
            alert.setHeaderText(null);
            alert.setContentText("New product registered with ID: " + prodId);
            alert.showAndWait();

            //Clear all fields
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

    @FXML
    private void handlesCancel(ActionEvent event) throws IOException{
        Stage dlg = (Stage)((Node)event.getSource()).getScene().getWindow();
        dlg.close();

    }

    @FXML
    private void handlesClear(ActionEvent event) {
        //Clear all fields
        txtProductName.clear();
        txtProductDescription.clear();
        txtPrice.clear();
        txtProductCode.clear();
        txtQuantity.clear();
        txtLocation.clear();
        cmbCategory.getSelectionModel().clearSelection();
    }
}