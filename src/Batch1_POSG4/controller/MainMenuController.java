package Batch1_POSG4.controller;

import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.scene.control.TextField;
import javafx.stage.Stage;
//import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.control.Alert;
import javafx.scene.Node;

public class MainMenuController {

    //sales
    @FXML
    private void handleSales(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSSales.fxml"));
        Parent mainLogin = loader.load();
        
        Stage stageSales = new Stage();
        stageSales.setTitle("Sales");
        stageSales.setScene(new Scene(mainLogin));
        stageSales.show();
        
        Stage menuStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        menuStage.close();        
    }

    //invetory
    @FXML
    private void handleInventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSInventory.fxml"));
        Parent mainInventory = loader.load();

        InventoryController controller = loader.getController();
        Stage stageInventory = new Stage();
        stageInventory.setTitle("Inventory");
        stageInventory.setScene(new Scene(mainInventory));
        stageInventory.setOnCloseRequest(e -> {
            e.consume();
            controller.handleClose(stageInventory, this);
        });

        stageInventory.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    //customers
    @FXML
    private void handleCustomer(ActionEvent event){
        
    }

    //Employees
    @FXML
    private void handleEmployees(ActionEvent event){
        
    }

    //Reports
    @FXML
    private void handleReports(ActionEvent event){
        
    }

    //Settings
    @FXML
    private void handleSettings(ActionEvent event){

    }

    //Tools/Utilities
    @FXML
    private void handleUtilitiesTools(ActionEvent event){

    }
    
    //exit
    @FXML
    private void handleLogoutExit(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSLogin.fxml"));
        Parent mainLogin = loader.load();
        
        Stage stageLogin = new Stage();
        stageLogin.setTitle("Log In");
        stageLogin.setScene(new Scene(mainLogin));
        stageLogin.show();
        
        Stage menuStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        menuStage.close();
    }
}
