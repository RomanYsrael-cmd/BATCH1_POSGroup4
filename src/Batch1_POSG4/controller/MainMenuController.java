package Batch1_POSG4.controller;

import java.io.IOException;

import Batch1_POSG4.util.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;

public class MainMenuController {

    long me = Session.get().getCurrentUser().getUserId();

    //sales
    @FXML
    private void handleSales(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSSales.fxml"));
        Parent mainSales = loader.load();

        SalesController controller = loader.getController();
        Stage stageSales = new Stage();
        stageSales.setTitle("Sales");
        stageSales.setScene(new Scene(mainSales));
        stageSales.setMaximized(true);
        stageSales.setOnCloseRequest(e -> {
            e.consume();
            controller.handleClose(stageSales, this);
        });

        stageSales.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
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
        stageInventory.setMaximized(true);
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
    private void handleEmployees(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSUsers.fxml"));
        Parent mainUsers = loader.load();

        UserController controller = loader.getController();
        Stage stageUsers = new Stage();
        stageUsers.setTitle("Users");
        stageUsers.setScene(new Scene(mainUsers));
        stageUsers.setMaximized(true);
        stageUsers.setOnCloseRequest(e -> {
            e.consume();
            controller.handleClose(stageUsers, this);
        });

        stageUsers.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
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
