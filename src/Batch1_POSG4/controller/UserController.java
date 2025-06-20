package Batch1_POSG4.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UserController {

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

}
