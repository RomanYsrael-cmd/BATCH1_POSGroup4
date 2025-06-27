package Batch1_POSG4.controller;

import java.io.IOException;

import Batch1_POSG4.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserController {
    long currentUserId = Session.get().getCurrentUser().getUserId();

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnClear;

    @FXML
    private ComboBox<?> cmbSelectRole;

    @FXML
    private TableView<?> tbl_Userlist;

    @FXML
    private PasswordField txtAdminPassword;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    void handlesAdd(ActionEvent event) {

    }

    @FXML
    void handlesCancel(ActionEvent event) {

    }

    @FXML
    void handlesClear(ActionEvent event) {

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
}
