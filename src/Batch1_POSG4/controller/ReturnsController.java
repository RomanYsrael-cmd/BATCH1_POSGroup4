package Batch1_POSG4.controller;

<<<<<<< Updated upstream
import javafx.event.ActionEvent;
=======
import Batch1_POSG4.dao.ReturnDAO;
import Batch1_POSG4.dao.SaleItemDAO;
import Batch1_POSG4.model.ReturnModel;
import Batch1_POSG4.view.SaleItemView;
import javafx.collections.ObservableList;
>>>>>>> Stashed changes
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

<<<<<<< Updated upstream
public class ReturnsController {
=======
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
>>>>>>> Stashed changes

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
    private ReturnDAO returnDAO;
    private SaleItemDAO saleItemDAO;
    private ObservableList<SaleItemView> saleItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DAO initialization
        returnDAO = new ReturnDAO(dbUrl);
        saleItemDAO = new SaleItemDAO(dbUrl);

        // Bind sale items list
        saleItems = tblTransactionList.getItems();

        // Set up table columns
        colProduct.setCellValueFactory(cell -> cell.getValue().productNameProperty());
        colUnitPrice.setCellValueFactory(cell -> cell.getValue().unitPriceProperty().asObject());
        colQtySold.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());
        colSubtotal.setCellValueFactory(cell -> cell.getValue().totalPriceProperty().asObject());

        tblTransactionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Listen for row selection
        tblTransactionList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) onSaleItemSelected(selected);
        });

        btnProcess.setDisable(true);
    }

    @FXML
    private void handlesSearchSale() {
        String txnNo = txtTransaction.getText().trim();
        if (txnNo.isEmpty()) {
            showAlert("Input Error", "Please enter a transaction number.", Alert.AlertType.WARNING);
            return;
        }
        saleItems.clear();
        try {
            long saleId = Long.parseLong(txnNo);
            saleItems.addAll(saleItemDAO.listItemsForSale(saleId));
            if (saleItems.isEmpty()) {
                showAlert("No Results", "No items found for transaction " + txnNo, Alert.AlertType.INFORMATION);
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Transaction number must be numeric.", Alert.AlertType.WARNING);
        } catch (SQLException ex) {
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void onSaleItemSelected(SaleItemView item) {
        txtItem.setText(item.getProductName());
        txtSoldQty.setText(String.valueOf(item.getQuantity()));
        txtRefund.setText(String.format("%.2f", item.getUnitPrice() * item.getQuantity()));
        chkRestock.setSelected(false);
        txtReason.clear();
        btnProcess.setDisable(false);
    }

    @FXML
    private void handlesProcess() {
        SaleItemView selected = tblTransactionList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String reason = txtReason.getText().trim();
        double refund;
        try {
            refund = Double.parseDouble(txtRefund.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid refund amount.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Process return and optionally restock
            long returnId = returnDAO.processReturn(
                selected.getSaleItemId(),
                reason,
                refund,
                chkRestock.isSelected()
            );
            // Remove the sale item record so it no longer appears
            saleItemDAO.removeItemFromSale(selected.getSaleItemId());

            // Remove from UI
            saleItems.remove(selected);

            ReturnModel ret = returnDAO.getReturnById(returnId);
            showAlert("Success", "Return processed (ID: " + ret.getReturnId() + ")", Alert.AlertType.INFORMATION);

            // Clear detail fields
            clearDetailFields();

            // Disable process if no more items
            if (saleItems.isEmpty()) btnProcess.setDisable(true);
        } catch (SQLException ex) {
            showAlert("DB Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlesCancel() {
        tblTransactionList.getSelectionModel().clearSelection();
        saleItems.clear();
        txtTransaction.clear();
        clearDetailFields();
        btnProcess.setDisable(true);
    }

    private void clearDetailFields() {
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
