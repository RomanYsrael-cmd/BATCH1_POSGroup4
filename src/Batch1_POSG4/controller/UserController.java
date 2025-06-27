package Batch1_POSG4.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import Batch1_POSG4.util.Sha256Hasher;

<<<<<<< Updated upstream
=======
import Batch1_POSG4.dao.UserDAO;
import Batch1_POSG4.model.User;
import Batch1_POSG4.util.Session;
import javafx.collections.ObservableList;
>>>>>>> Stashed changes
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyStringWrapper;

<<<<<<< Updated upstream
public class UserController {
=======
>>>>>>> Stashed changes

public class UserController implements Initializable {
    private static final String DB_URL = "jdbc:sqlite:db/db_pos_g4.db";


    @FXML private TableView<User> tbl_Userlist;
    @FXML private TableColumn<User,Long>   colUserId;
    @FXML private TableColumn<User,String> colUsername;
    @FXML private TableColumn<User,String> colRole;
    @FXML private TableColumn<User,String> colCreated;

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbSelectRole;
    @FXML private PasswordField txtAdminPassword;
    @FXML private Button        btnAdd;
    @FXML private Button        btnClear;
    @FXML private Button        btnCancel;

    private UserDAO userDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbSelectRole.getItems().addAll("ADMIN", "EMPLOYEE", "DEVELOPER");
        cmbSelectRole.getSelectionModel().selectFirst();

        colUserId  .setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole    .setCellValueFactory(new PropertyValueFactory<>("role"));
        colCreated .setCellValueFactory(user -> {
            var ts = user.getValue().getCreatedAt();
            String txt = ts.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new ReadOnlyStringWrapper(txt);
        });

        userDao = new UserDAO(DB_URL);
        refreshTable();
    }

    private void refreshTable() {
        try {
            ObservableList<User> all = userDao.listAllUsers();
            tbl_Userlist.setItems(all);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR,
                      "Could not load users:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    void handlesAdd(ActionEvent event) {
        String user     = txtUsername.getText().trim();
        String pass     = txtPassword.getText();
        String role     = cmbSelectRole.getValue();
        String adminPwd = txtAdminPassword.getText();
        Sha256Hasher hasher = new Sha256Hasher(adminPwd);


        if (user.isEmpty() || pass.isEmpty() || adminPwd.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "All fields are required.").showAndWait();
            return;
        }

        var me = Session.get().getCurrentUser();
        if (!hasher.getHashedOutput().equals(me.getPasswordHash())) {
            new Alert(Alert.AlertType.ERROR, "Invalid admin password.").showAndWait();
            return;
        }

        try {
            Sha256Hasher pass2 = new Sha256Hasher(pass);
            userDao.addUser(user, pass2.getHashedOutput(), role);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR,
                      "Could not add user:\n" + ex.getMessage()
            ).showAndWait();
            return;
        }

        txtUsername.clear();
        txtPassword.clear();
        txtAdminPassword.clear();
        cmbSelectRole.getSelectionModel().selectFirst();
        refreshTable();

        new Alert(Alert.AlertType.INFORMATION,
                  "User “" + user + "” added."
        ).showAndWait();
    }

    @FXML
    void handlesClear(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        txtAdminPassword.clear();
        cmbSelectRole.getSelectionModel().selectFirst();
    }

    @FXML
    void handlesCancel(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Main Menu");
    }

    @FXML
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
}
