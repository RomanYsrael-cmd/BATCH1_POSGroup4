package Batch1_POSG4.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Batch1_POSG4.dao.UserDAO;
import Batch1_POSG4.model.User;
import Batch1_POSG4.util.Session;
import Batch1_POSG4.util.Sha256Hasher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class UserController implements Initializable{
    long currentUserId = Session.get().getCurrentUser().getUserId();

    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";

    @FXML private TableView<User>       tbl_Userlist;
    @FXML private TableColumn<User,Long>   colUserId;
    @FXML private TableColumn<User,String> colUsername;
    @FXML private TableColumn<User,String> colRole;
    @FXML private TableColumn<User,String> colCreatedAt;

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtAdminPassword;
    @FXML private ComboBox<String> cmbSelectRole;

    @FXML private Button btnAdd;
    @FXML private Button btnClear;
    @FXML private Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) wire up the columns
        colUserId   .setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername .setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole     .setCellValueFactory(new PropertyValueFactory<>("role"));
        // assuming your User model has a getCreatedAt() that returns LocalDateTime,
        // you can display it as a string:
        colCreatedAt.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getCreatedAt().toString())
        );

        // 2) fill your role dropdown
        cmbSelectRole.getItems().addAll("ADMIN", "DEVELOPER", "EMPLOYEE");

        // 3) load data right away
        refreshTable();
    }


    @FXML
    void handlesAdd(ActionEvent event) {
        if (txtUsername.getText().isBlank()
         || txtPassword.getText().isBlank()
         || txtAdminPassword.getText().isBlank()
         || cmbSelectRole.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").showAndWait();
            return;
        }

        // verify admin password
        String adminHash = new Sha256Hasher(txtAdminPassword.getText()).getHashedOutput();
        if (!adminHash.equals(Session.get().getCurrentUser().getPasswordHash())) {
            new Alert(Alert.AlertType.ERROR, "Invalid admin password").showAndWait();
            return;
        }

        String username = txtUsername.getText().trim();
        String role     = cmbSelectRole.getValue();
        String userHash = new Sha256Hasher(txtPassword.getText()).getHashedOutput();

        try {
            UserDAO dao = new UserDAO(dbUrl);
            dao.addUser(username, userHash, role);
            refreshTable();
            handlesClear(event);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to add user:\n" + ex.getMessage()).showAndWait();
        }

    }

    @FXML
    void handlesCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
            Parent main = loader.load();
            Stage m = new Stage();
            m.setScene(new Scene(main));
            m.setTitle("Main Menu");
            m.show();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handlesClear(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        txtAdminPassword.clear();
        cmbSelectRole.getSelectionModel().clearSelection();

    }

    public void handleClose(Stage userStage, MainMenuController caller) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
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
    private void refreshTable() {
        try {
            UserDAO dao = new UserDAO(dbUrl);
            ObservableList<User> users = dao.listAllUsers();
            tbl_Userlist.setItems(users);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load users:\n" + e.getMessage()).showAndWait();
        }
    }


}
