package Batch1_POSG4.controller;

// Standard library imports
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

// Third-party packages
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

// Project-specific imports
import Batch1_POSG4.dao.UserDAO;
import Batch1_POSG4.model.User;
import Batch1_POSG4.util.Session;
import Batch1_POSG4.util.Sha256Hasher;

// Manages user administration, including adding, removing, and listing users.
public class UserController implements Initializable {

    // Instance fields (public)

    // Instance fields (private)
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    long currentUserId = Session.get().getCurrentUser().getUserId();

    @FXML private TableView<User> tbl_Userlist;
    @FXML private TableColumn<User,Long> colUserId;
    @FXML private TableColumn<User,String> colUsername;
    @FXML private TableColumn<User,String> colRole;
    @FXML private TableColumn<User,String> colCreatedAt;

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtAdminPassword;
    @FXML private ComboBox<String> cmbSelectRole;

    @FXML private Button btnAdd;
    @FXML private Button btnClear;
    @FXML private Button btnCancel;

    // Initializes the user controller, sets up table columns, role dropdown, and loads user data.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colCreatedAt.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getCreatedAt().toString())
        );
        cmbSelectRole.getItems().addAll("ADMIN", "DEVELOPER", "EMPLOYEE");
        refreshTable();
    }

    // Handles adding a new user after validating input and admin password.
    @FXML
    void handlesAdd(ActionEvent event) {
        if (txtUsername.getText().isBlank()
         || txtPassword.getText().isBlank()
         || txtAdminPassword.getText().isBlank()
         || cmbSelectRole.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").showAndWait();
            return;
        }
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

    // Handles cancel action, returning to the main menu.
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

    // Clears all input fields and resets the role selection.
    @FXML
    void handlesClear(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        txtAdminPassword.clear();
        cmbSelectRole.getSelectionModel().clearSelection();
    }

    // Handles removing a user after password confirmation and user selection.
    @FXML
    private void handlesRemoveUser(ActionEvent event) {
        User selected = tbl_Userlist.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to remove.").showAndWait();
            return;
        }
        if (selected.getUserId() == currentUserId) {
            new Alert(Alert.AlertType.ERROR, "You cannot remove your own account while logged in.").showAndWait();
            return;
        }
        Dialog<String> pwdDialog = new Dialog<>();
        pwdDialog.setTitle("Confirm Deletion");
        pwdDialog.setHeaderText("Enter your password to confirm deletion of \"" 
                                + selected.getUsername() + "\"");
        ButtonType deleteBtnType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        pwdDialog.getDialogPane().getButtonTypes().addAll(deleteBtnType, ButtonType.CANCEL);
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("Current admin password");
        pwdDialog.getDialogPane().setContent(pwdField);
        Node deleteButton = pwdDialog.getDialogPane().lookupButton(deleteBtnType);
        deleteButton.setDisable(true);
        pwdField.textProperty().addListener((obs, old, val) -> 
            deleteButton.setDisable(val.trim().isEmpty())
        );
        pwdDialog.setResultConverter(btn -> {
            if (btn == deleteBtnType) {
                return pwdField.getText();
            }
            return null;
        });
        Optional<String> maybePwd = pwdDialog.showAndWait();
        maybePwd.ifPresent(enteredPwd -> {
            String enteredHash = new Sha256Hasher(enteredPwd).getHashedOutput();
            String actualHash  = Session.get().getCurrentUser().getPasswordHash();
            if (!enteredHash.equals(actualHash)) {
                new Alert(Alert.AlertType.ERROR, "Invalid password—deletion cancelled.").showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Really delete user \"" + selected.getUsername() + "\"?");
            confirm.setHeaderText("Please Confirm");
            confirm.showAndWait()
                .filter(b -> b == ButtonType.OK)
                .ifPresent(b -> {
                    try {
                        new UserDAO(dbUrl).deleteUser(selected.getUserId());
                        refreshTable();
                    } catch (SQLException ex) {
                        new Alert(Alert.AlertType.ERROR,
                            "Failed to delete user:\n" + ex.getMessage()).showAndWait();
                    }
                });
        });
    }

    // Closes the user management screen and returns to the main menu.
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

    // Loads all users from the database and populates the table.
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