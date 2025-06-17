package Batch1_POSG4.controller;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private void handleAddItem(ActionEvent event) throws IOException{
    //Load AddInventoryUI
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSAddInventory.fxml"));
    Parent mainInventory = loader.load();
    AddInventoryController controller = loader.getController();

    Stage stageInventory = new Stage();
    stageInventory.setTitle("Add Inventory");
    stageInventory.setScene(new Scene(mainInventory));

    //Set as modal dialog
    stageInventory.initModality(Modality.APPLICATION_MODAL);

    //Set owner
    Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stageInventory.initOwner(owner);

    // Load categories
    controller.loadCategories();

    // Show as modal dialog and wait
    stageInventory.showAndWait();
        
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