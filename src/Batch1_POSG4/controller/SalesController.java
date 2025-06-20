package Batch1_POSG4.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SalesController {

    @FXML
    private Button btnBarcode;

    @FXML
    private Button btnCategory;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnDiscount;

    @FXML
    private Button btnEditQuantity;

    @FXML
    private Button btnItem1;

    @FXML
    private Button btnItem2;

    @FXML
    private Button btnItem3;

    @FXML
    private Button btnItem4;

    @FXML
    private Button btnItem5;

    @FXML
    private Button btnItem6;

    @FXML
    private Button btnItem7;

    @FXML
    private Button btnItem8;

    @FXML
    private Button btnItem9;

    @FXML
    private Button btnManageDiscount;

    @FXML
    private Button btnOpenCash;

    @FXML
    private Button btnPrintReciept;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnVoid;

    @FXML
    private CheckBox chkPDWSenior;

    @FXML
    private ComboBox<?> cmbCategory;

    @FXML
    private Label lblDatTime;

    @FXML
    private ListView<?> lstViewItems;

    @FXML
    private TableView<?> tblSales;

    @FXML
    private TableView<?> tblTotals;

    @FXML
    private TextField txtBarcode;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtDiscount;

    @FXML
    private TextField txtSearchItem;

    @FXML
    private TextField txtTransactionNumber;

    @FXML
    void handlesBarcode(ActionEvent event) {

    }

    @FXML
    void handlesCustomer(ActionEvent event) {

    }

    @FXML
    void handlesDiscount(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/Batch1_POSG4/view/POSDiscount.fxml"
        ));
        Parent addInvRoot = loader.load();
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(addInvRoot));
        dialog.setTitle("Discount Manager");
        dialog.showAndWait();
    }

    @FXML
    void handlesEditQuantity(ActionEvent event) {

    }

    @FXML
    void handlesItem1(ActionEvent event) {

    }

    @FXML
    void handlesItem2(ActionEvent event) {

    }

    @FXML
    void handlesItem3(ActionEvent event) {

    }

    @FXML
    void handlesItem4(ActionEvent event) {

    }

    @FXML
    void handlesItem5(ActionEvent event) {

    }

    @FXML
    void handlesItem6(ActionEvent event) {

    }

    @FXML
    void handlesItem7(ActionEvent event) {

    }

    @FXML
    void handlesItem8(ActionEvent event) {

    }

    @FXML
    void handlesItem9(ActionEvent event) {

    }

    @FXML
    void handlesManageDiscount(ActionEvent event) throws IOException {

    }

    @FXML
    void handlesOpenCash(ActionEvent event) {

    }

    @FXML
    void handlesPDWSenior(ActionEvent event) {

    }

    @FXML
    void handlesPrintReciept(ActionEvent event) {

    }

    @FXML
    void handlesReturn(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/Batch1_POSG4/view/POSReturn.fxml"
        ));
        Parent addInvRoot = loader.load();
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(addInvRoot));
        dialog.setTitle("Returns Manager");
        dialog.showAndWait();
    }
    
    @FXML
    void handlesSearch(ActionEvent event) {

    }

    @FXML
    void handlesVoid(ActionEvent event) {

    }
    
    @FXML
    public void handleClose(Stage salesStage, MainMenuController caller) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
            Parent mainMenu = loader.load();
            Stage mainMenuStage = new Stage();
            mainMenuStage.setScene(new Scene(mainMenu));
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();
            salesStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
