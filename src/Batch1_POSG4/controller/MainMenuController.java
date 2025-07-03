package Batch1_POSG4.controller;

// Standard library imports
import java.io.IOException;

// Third-party packages
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Project-specific imports
import Batch1_POSG4.util.Session;

// Controls the main menu, handling navigation to sales, inventory, customers, users, reports, settings, utilities, and logout.
public class MainMenuController {

    // Instance fields (public)

    // Instance fields (private)
    long me = Session.get().getCurrentUser().getUserId();

    // Opens the sales screen and closes the main menu.
    @FXML
    private void handleSales(ActionEvent event) throws IOException {
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

    // Opens the inventory screen and closes the main menu.
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

    // Opens the customer management screen and closes the main menu.
    @FXML
    private void handleCustomer(ActionEvent event) throws IOException {
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

    // Opens the user management screen and closes the main menu.
    @FXML
    private void handleEmployees(ActionEvent event) throws IOException {
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

    // Opens the monthly sales report dialog as a modal window.
    @FXML
    private void handleReports(ActionEvent event) throws IOException {
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

    // Handles opening the settings screen (not implemented).
    @FXML
    private void handleSettings(ActionEvent event) {
    }

    // Handles opening the utilities/tools screen (not implemented).
    @FXML
    private void handleUtilitiesTools(ActionEvent event) {
    }

    // Logs out the current user and returns to the login screen.
    @FXML
    private void handleLogoutExit(ActionEvent event) throws IOException {
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