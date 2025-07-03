package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import Batch1_POSG4.dao.CustomerDAO;
import Batch1_POSG4.model.Customer;

// Manages customer selection, addition, and dialog interactions for the POS system.
public class CustomerController {

    // Instance fields (public)
    @FXML public Button btnSelectCustomer;

    // Instance fields (private)
    @FXML private TableView<Customer> tblCustomers;
    @FXML private TableColumn<Customer, Integer> colCustomerID;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, Integer> colLoyalty;
    @FXML private Button btnAddNewCustomer;
    @FXML private Button btnCancel;
    @FXML private Button btnClear;
    @FXML private TextField txtEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtPhone;

    private Customer selectedCustomer;
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";

    // Initializes the customer table columns and loads all customers from the database.
    @FXML
    public void initialize() {
        colCustomerID.setCellValueFactory(cell -> 
            new ReadOnlyObjectWrapper<>(cell.getValue().getCustomerId()));
        colName.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getName()));
        colEmail.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getEmail()));
        colPhone.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getPhone()));
        colLoyalty.setCellValueFactory(cell ->
            new ReadOnlyObjectWrapper<>(cell.getValue().getLoyaltyPoints()));

        try {
            ObservableList<Customer> data = new CustomerDAO(dbUrl).fetchAll();
            tblCustomers.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles selecting a customer from the table and closes the dialog if a selection is made.
    @FXML
    private void handlesSelectCustomer() {
        this.selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Stage stage = (Stage) btnSelectCustomer.getScene().getWindow();
            stage.close();
        } else {
            // TODO: alert "Please pick a customer first"
        }
    }

    // Returns the customer selected by the user after the dialog closes.
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    // Handles adding a new customer to the database and updates the table.
    @FXML
    void handlesAddNewCustomer(ActionEvent event) {
        String name  = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            return;
        }

        try {
            Customer newCust = new CustomerDAO(dbUrl).create(name, email, phone);
            tblCustomers.getItems().add(newCust);
            tblCustomers.getSelectionModel().select(newCust);
            handlesClear(event);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Clears the customer input fields.
    @FXML
    private void handlesClear(ActionEvent event) {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
    }

    // Handles canceling the dialog, closing the window without selection.
    @FXML
    private void handlesCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    // Closes the customer dialog and returns to the main menu.
    public void handleClose(Stage userStage, MainMenuController caller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
            btnSelectCustomer.setDisable(false);    
            Parent mainMenu = loader.load();
            Stage mainMenuStage = new Stage();
            mainMenuStage.setScene(new Scene(mainMenu));
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();
            userStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}