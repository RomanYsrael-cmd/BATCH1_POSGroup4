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
<<<<<<< Updated upstream
    public void handleClose(Stage salesStage, MainMenuController caller) {
=======
    void handlesProceedPayment(ActionEvent event) {
        String payText = txtPayment.getText().trim();
        if (payText.isEmpty()) {
            showAlert("Payment Required", "Please enter the amount received.");
            return;
        }

        double paid = Double.parseDouble(txtPayment.getText().trim());
        try {
            paid = Double.parseDouble(payText);
        } catch (NumberFormatException ex) {
            showAlert("Invalid Amount", "Please enter a valid numeric payment.");
            return;
        }

        double grandTotal = totalsData.get(2).getAmount();
        if (paid < grandTotal) {
            showAlert("Insufficient Payment", 
                    String.format("Paid ₱%.2f is less than total ₱%.2f.", paid, grandTotal));
            return;
        }

        if (totalsData.size() < 3) {
            showAlert("Nothing to pay", "Please scan items first.");
            return;
        }

        if (paid < grandTotal) {
        showAlert("Insufficient Payment", "Payment error");
        return;
        }
        txtChange.setText(String.format("₱%.2f", paid - grandTotal));

        // 2) Now persist everything in one go
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            conn.setAutoCommit(false);

            // a) insert each line-item
            String insertSQL = """
            INSERT INTO tbl_SaleItem(sale_id, product_id, quantity, unit_price, total_price)
            VALUES (?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                for (SaleItemView item : saleItems) {
                    ps.setLong   (1, currentSaleId);
                    ps.setLong   (2, item.getProductId());
                    ps.setInt    (3, item.getQuantity());
                    ps.setDouble (4, item.getUnitPrice());
                    ps.setDouble (5, item.getTotalPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // b) update the sale header with real total & payment method
            String updSQL = """
            UPDATE tbl_Sale
                SET total_amount   = ?,
                    payment_method = ?
            WHERE sale_id = ?
            """;
            try (PreparedStatement ps2 = conn.prepareStatement(updSQL)) {
                ps2.setDouble(1, grandTotal);
                ps2.setString(2, paymentMethod);
                ps2.setLong  (3, currentSaleId);
                ps2.executeUpdate();
            }
            InventoryAddDAO invDao = new InventoryAddDAO(dbUrl);
            for (SaleItemView item : saleItems) {
                // subtract sold qty:
                invDao.adjustInventory(conn, item.getProductId(), -item.getQuantity());
            }

            
            conn.commit();
            showAlert("Payment Complete",
                    String.format("Transaction #%d OK. Change: ₱%.2f",
                                    currentSaleId, paid - grandTotal));

            // disable further editing for this sale
            btnProceedPayment.setDisable(true);
            txtBarcode      .setDisable(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Database Error", ex.getMessage());
        }
    }

    @FXML
    void handlesNewTransaction(ActionEvent event) {
>>>>>>> Stashed changes
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
