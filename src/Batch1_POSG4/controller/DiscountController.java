package Batch1_POSG4.controller;

import Batch1_POSG4.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DiscountController {
    long currentUserId = Session.get().getCurrentUser().getUserId();

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSubmit;

    @FXML
    private TableView<?> tblDiscountTable;

    @FXML
    private TextField txtAmmount;

    @FXML
    private TextField txtDiscountCode;

    @FXML
    private ComboBox<?> txtItem;

    @FXML
    private ComboBox<?> txtType;

    @FXML
    void handlesCancel(ActionEvent event) {

    }

    @FXML
    void handlesSubmit(ActionEvent event) {

    }

}
