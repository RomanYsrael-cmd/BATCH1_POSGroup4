package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.SQLException;

import Batch1_POSG4.dao.LoginSessionDAO;
import Batch1_POSG4.util.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

public class MainMenuController {
    @FXML private AnchorPane rootPane;

    long me = Session.get().getCurrentUser().getUserId();
    private void logoutAndShowLogin(Stage currentStage) {
        long sid = Session.get().getSessionId();
        try {
            new LoginSessionDAO().closeSession(sid);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            Parent login = FXMLLoader.load(
                getClass().getResource("/Batch1_POSG4/view/POSLogin.fxml")
            );
            Stage loginStage = new Stage();
            loginStage.setTitle("Log In");
            loginStage.setScene(new Scene(login));
            loginStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        currentStage.close();
    }
    @FXML
    public void initialize() {
        // runLater to ensure scene & window have been set
        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                evt.consume();   // prevent default hide()
                logoutAndShowLogin(stage);
            });
        });
    }


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
    private void handleCustomer(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSCustomer.fxml"));
        Parent mainCustomer = loader.load();

        CustomerController controller = loader.getController();
        Stage stageUsers = new Stage();
        stageUsers.setTitle("Customer");
        stageUsers.setScene(new Scene(mainCustomer));
        stageUsers.setMaximized(true);
        stageUsers.setOnCloseRequest(e -> {
            e.consume();
            controller.handleClose(stageUsers, this);
            controller.btnSelectCustomer.setDisable(true);
        });

        stageUsers.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
       
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
    private void handleReports(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/Batch1_POSG4/view/POSReport.fxml"
        ));
        Parent root = loader.load();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(root));
        dialog.setTitle("Monthly Sales Report");
        dialog.showAndWait();
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
    private void handleLogoutExit(ActionEvent event) {
        Stage menuStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        logoutAndShowLogin(menuStage);
    }

}
