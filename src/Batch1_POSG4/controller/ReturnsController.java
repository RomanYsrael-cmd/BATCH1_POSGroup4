package Batch1_POSG4.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReturnsController {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnProcess;

    @FXML
    private CheckBox chkRestock;

    @FXML
    private TableView<?> tblPastReturns;

    @FXML
    private TableView<?> tblTransactionList;

    @FXML
    private TextField txtItem;

    @FXML
    private TextField txtReason;

    @FXML
    private TextField txtRefund;

    @FXML
    private TextField txtSearchSale;

    @FXML
    private TextField txtSoldQty;

    @FXML
    private TextField txtTransaction;

    @FXML
    void handlesCancel(ActionEvent event) {

    }

    @FXML
    void handlesProcess(ActionEvent event) {

    }

}