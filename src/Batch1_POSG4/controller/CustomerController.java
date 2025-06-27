package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.SQLException;

import Batch1_POSG4.dao.CustomerDAO;

import Batch1_POSG4.model.Customer;
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

public class CustomerController {
    @FXML private TableView<Customer> tblCustomers;
    @FXML private TableColumn<Customer, Integer> colCustomerID;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, Integer> colLoyalty;
    @FXML public Button btnSelectCustomer;

    private Customer selectedCustomer;
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";

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

        // load data
        try {
            ObservableList<Customer> data = new CustomerDAO(dbUrl).fetchAll();
            tblCustomers.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    /** Called by the caller after showAndWait() */
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    @FXML
    private Button btnAddNewCustomer;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnClear;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    void handlesAddNewCustomer(ActionEvent event) {
        String name  = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            // show alert: “Name is required”
            return;
        }

        try {
            Customer newCust = new CustomerDAO(dbUrl).create(name, email, phone);
            // reload table so the new one appears:
            tblCustomers.getItems().add(newCust);

            // optionally auto-select the newly added row:
            tblCustomers.getSelectionModel().select(newCust);

            // clear the form
            handlesClear(event);

        } catch (SQLException ex) {
            ex.printStackTrace();
            // show error to user…
        }
    }

    @FXML
    private void handlesClear(ActionEvent event) {
        // clear the “Add New Customer” form fields:
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
    }

    @FXML
    private void handlesCancel(ActionEvent event) {
        // simply close the dialog without selecting
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

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
