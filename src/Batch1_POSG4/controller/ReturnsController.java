package Batch1_POSG4.controller;

import Batch1_POSG4.dao.ReturnDAO;
import Batch1_POSG4.dao.SaleItemDAO;
import Batch1_POSG4.model.ReturnModel;
import Batch1_POSG4.view.SaleItemView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReturnsController implements Initializable {

    @FXML private TextField txtTransaction;
    @FXML private Button btnSearchSale;
    @FXML private TableView<SaleItemView> tblTransactionList;
    @FXML private TableColumn<SaleItemView, String> colProduct;
    @FXML private TableColumn<SaleItemView, Double> colUnitPrice;
    @FXML private TableColumn<SaleItemView, Integer> colQtySold;
    @FXML private TableColumn<SaleItemView, Double> colSubtotal;

    @FXML private TextField txtItem;
    @FXML private TextField txtSoldQty;
    @FXML private TextField txtReason;
    @FXML private TextField txtRefund;
    @FXML private CheckBox chkRestock;
    @FXML private Button btnProcess;
    @FXML private Button btnCancel;

    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private SaleItemDAO saleItemDAO;
    private ReturnDAO returnDAO;
    private ObservableList<SaleItemView> saleItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize DAOs
        saleItemDAO = new SaleItemDAO(dbUrl);
        returnDAO   = new ReturnDAO(dbUrl);

        // Bind TableView items
        saleItems = tblTransactionList.getItems();

        // Configure columns
        colProduct.setCellValueFactory(cell -> cell.getValue().productNameProperty());
        colUnitPrice.setCellValueFactory(cell -> cell.getValue().unitPriceProperty().asObject());
        colQtySold.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());
        colSubtotal.setCellValueFactory(cell -> cell.getValue().totalPriceProperty().asObject());

        // Allow single-row selection
        tblTransactionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblTransactionList.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldSel, newSel) -> {
                if (newSel != null) populateDetails(newSel);
            });

        // Disable process until an item is selected
        btnProcess.setDisable(true);
    }

    @FXML
    private void handlesSearchSale() {
        String txn = txtTransaction.getText().trim();
        if (txn.isEmpty()) {
            showAlert("Input Error", "Please enter a transaction number.", Alert.AlertType.WARNING);
            return;
        }
        saleItems.clear();
        try {
            long saleId = Long.parseLong(txn);
            saleItems.addAll(saleItemDAO.listItemsForSale(saleId));
            if (saleItems.isEmpty()) {
                showAlert("No Results", "No items found for transaction " + txn, Alert.AlertType.INFORMATION);
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Transaction ID must be numeric.", Alert.AlertType.WARNING);
        } catch (SQLException e) {
            showAlert("DB Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void populateDetails(SaleItemView item) {
        txtItem.setText(item.getProductName());
        txtSoldQty.setText(String.valueOf(item.getQuantity()));
        txtRefund.setText(String.format("%.2f", item.getUnitPrice() * item.getQuantity()));
        txtReason.clear();
        chkRestock.setSelected(false);
        btnProcess.setDisable(false);
    }

    @FXML
    private void handlesProcess() {
        SaleItemView selected = tblTransactionList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String reason = txtReason.getText().trim();
        double refund;
        try {
            refund = Double.parseDouble(txtRefund.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid refund amount.", Alert.AlertType.WARNING);
            return;
        }

        try {
            long returnId = returnDAO.processReturn(
                selected.getSaleItemId(),
                reason,
                refund,
                chkRestock.isSelected()
            );

            // Remove the returned sale item
            saleItemDAO.removeItemFromSale(selected.getSaleItemId());
            saleItems.remove(selected);

            // Show confirmation
            ReturnModel ret = returnDAO.getReturnById(returnId);
            showAlert("Success", "Return processed (ID: " + ret.getReturnId() + ")", Alert.AlertType.INFORMATION);

            clearDetails();
            btnProcess.setDisable(saleItems.isEmpty());
        } catch (SQLException e) {
            showAlert("DB Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlesCancel() {
        tblTransactionList.getSelectionModel().clearSelection();
        saleItems.clear();
        txtTransaction.clear();
        clearDetails();
        btnProcess.setDisable(true);
    }

    private void clearDetails() {
        txtItem.clear();
        txtSoldQty.clear();
        txtReason.clear();
        txtRefund.clear();
        chkRestock.setSelected(false);
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
