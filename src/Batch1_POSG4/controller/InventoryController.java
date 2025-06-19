package Batch1_POSG4.controller;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;

import java.io.IOException;

import Batch1_POSG4.dao.ProductDAO;
import Batch1_POSG4.view.ProductView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.TextField;
import javafx.stage.Stage;
//import javafx.scene.control.PasswordField;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
//import javafx.scene.control.Alert;
import javafx.scene.Node;
//import javafx.stage.Stage;

public class InventoryController {

    @FXML public TableView<ProductView> tblInventory;
    @FXML public TableColumn<ProductView, Integer> colProductCode;
    @FXML public TableColumn<ProductView, String> colProductName;
    @FXML public TableColumn<ProductView, String> colCategory;
    @FXML public TableColumn<ProductView, Integer> colQuantity;
    @FXML public TableColumn<ProductView, Double> colPrice;
    @FXML public TableColumn<ProductView, String> colBarcode;

    @FXML
    public void initialize() {
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        ProductDAO dao = new ProductDAO();
        ObservableList<ProductView> list = dao.fetchInventoryWithCategory();
        tblInventory.setItems(list);
    }
    @FXML
    public void populate(){
    }
    public void handleClose(Stage inventoryStage, MainMenuController caller) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
            Parent mainMenu = loader.load();
            Stage mainMenuStage = new Stage();
            mainMenuStage.setScene(new Scene(mainMenu));
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();
            inventoryStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private void refreshInventoryTable() {
        // you can also re-wire your columns here if you like,
        // but since initialize() already did it, just reset items:
        ObservableList<ProductView> list = new ProductDAO().fetchInventoryWithCategory();
        tblInventory.setItems(list);
    }
    @FXML
    private void handleSearch(KeyEvent event) {
        // filter logic
    }

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        // filter by category
    }

    @FXML
    private void handleOtherFilter(ActionEvent event) {
        // filter by other options
    }

    @FXML
    private void handleAddItem(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/Batch1_POSG4/view/POSAddInventory.fxml"
        ));
        Parent addInvRoot = loader.load();
        AddInventoryController addCtrl = loader.getController();

        // load categories in the dialog  
        addCtrl.loadCategories();

        // show as modal
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(addInvRoot));
        dialog.setTitle("Add Inventory");
        dialog.showAndWait();

        // ← this runs *after* the add‐inventory dialog closes:
        refreshInventoryTable();
    }

    @FXML
    private void handlePrevPage(ActionEvent event) {
        // load previous page
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        // load next page
    }
}