package Batch1_POSG4.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import Batch1_POSG4.dao.DiscountDAO;
import Batch1_POSG4.dao.ProductDAO;
import Batch1_POSG4.model.Discount;
import Batch1_POSG4.view.ProductView;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DiscountController implements Initializable {

    @FXML private TableView<Discount>    tblDiscountTable;
    @FXML private TextField              txtDiscountCode;
    @FXML private ComboBox<String>       cmbType;
    @FXML private TextField              txtAmmount;
    @FXML private TextField              txtDescription;
    @FXML private ComboBox<ProductView>  cmbItem;
    @FXML private Button                 btnSubmit;
    @FXML private Button                 btnCancel;
    @FXML private TableColumn<Discount,String> colCode;
    @FXML private TableColumn<Discount,String>     colItem; 
    @FXML private TableColumn<Discount,String> colDescription;
    @FXML private TableColumn<Discount,String> colType;
    @FXML private TableColumn<Discount,Double> colAmount;

     private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private DiscountDAO    discountDAO;
    private long  currentSaleId;
    private ObservableList<Discount>    discounts = FXCollections.observableArrayList();
    private ObservableList<ProductView> products  = FXCollections.observableArrayList();

    public void setSaleId(long saleId) {
        this.currentSaleId = saleId;
        loadDiscounts();
    }

     @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) DAO
        discountDAO = new DiscountDAO(dbUrl);

        // 2) configure columns
        colCode
          .setCellValueFactory(new PropertyValueFactory<>("discountCode"));
        colItem
          .setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDescription
          .setCellValueFactory(new PropertyValueFactory<>("description"));
        colType
          .setCellValueFactory(new PropertyValueFactory<>("discountType"));
        colAmount
          .setCellValueFactory(new PropertyValueFactory<>("amount"));

        tblDiscountTable.setItems(discounts);

        // 3) populate “Item” dropdown
        try {
            products.addAll(
                new ProductDAO().search("", "Name", null)
            );
            cmbItem.setItems(products);

            cmbItem.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(ProductView p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null 
                            ? "" 
                            : p.getProductName());
                }
            });
            cmbItem.setButtonCell(cmbItem.getCellFactory().call(null));
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Failed to load products:\n" + ex.getMessage());
        }

        // 4) populate “Type” dropdown
        cmbType.getItems().setAll("Percentage", "Fixed Amount");

        // 5) wire buttons
        btnSubmit .setOnAction(this::handlesSubmit);
        btnCancel .setOnAction(this::handlesCancel);

        // 6) initial load of all discounts
        loadDiscounts();
        tblDiscountTable.setRowFactory(tv -> {
            TableRow<Discount> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    showEditDiscountDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void loadDiscounts() {
        discounts.clear();
        try {
            discounts.addAll(discountDAO.fetchAll());
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Could not load discounts:\n" + ex.getMessage());
        }
    }

    @FXML
    private void handlesSubmit(ActionEvent ev) {
        // 1) gather form data
        Discount d = new Discount();
        d.setDiscountCode(txtDiscountCode.getText().trim());
        d.setDescription(txtDescription.getText().trim());
        d.setDiscountType(cmbType.getValue());
        try {
            d.setAmount(Double.parseDouble(txtAmmount.getText().trim()));
        } catch (NumberFormatException ex) {
            showAlert("Invalid Amount", "Please enter a valid number.");
            return;
        }

        ProductView pv = cmbItem.getValue();
        if (pv != null) {
            d.setProductId(pv.getProductCode());
            d.setProductName(pv.getProductName());
        }

        // 2) persist & refresh
        try {
            discountDAO.addDiscount(d);
            clearForm();
            loadDiscounts();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Could not save discount:\n" + ex.getMessage());
        }
    }

    @FXML
    private void handlesCancel(ActionEvent ev) {
        Stage st = (Stage) btnCancel.getScene().getWindow();
        st.close();
    }

    /** Clears all form inputs. */
    private void clearForm() {
        txtDiscountCode.clear();
        txtDescription.clear();
        txtAmmount.clear();
        cmbType.getSelectionModel().clearSelection();
        cmbItem.getSelectionModel().clearSelection();
    }

    /** Convenience alert. */
    private void showAlert(String header, String body) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Discount Manager");
        a.setHeaderText(header);
        a.setContentText(body);
        a.showAndWait();
    }

    private void showEditDiscountDialog(Discount d) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Edit Discount");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField codeField    = new TextField(d.getDiscountCode());
        ComboBox<String> typeBox   = new ComboBox<>(FXCollections.observableArrayList("Percentage", "Fixed Amount"));
        typeBox.setValue(d.getDiscountType());
        TextField amountField  = new TextField(Double.toString(d.getAmount()));
        TextField descField    = new TextField(d.getDescription());
        ComboBox<ProductView> itemBox = new ComboBox<>(products);
        itemBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(ProductView p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty||p==null ? "" : p.getProductName());
            }
        });
        itemBox.setButtonCell(itemBox.getCellFactory().call(null));

        if (d.getProductId() != null) {
            products.stream()
                    .filter(pv -> pv.getProductCode() == d.getProductId())
                    .findFirst()
                    .ifPresent(itemBox::setValue);
        }

        grid.addRow(0, new Label("Code:"),             codeField);
        grid.addRow(1, new Label("Type:"),             typeBox);
        grid.addRow(2, new Label("Amount:"),           amountField);
        grid.addRow(3, new Label("Description:"),      descField);
        grid.addRow(4, new Label("Item (optional):"),  itemBox);

        dlg.getDialogPane().setContent(grid);

        Optional<ButtonType> res = dlg.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            String newCode = codeField.getText().trim();
            try {
                if (newCode.isEmpty()) {
                    // DELETE if user cleared the code
                    discountDAO.deleteByCode(d.getDiscountCode());
                } else {
                    // parse & validate amount
                    double newAmt = Double.parseDouble(amountField.getText().trim());

                    // update the Discount object
                    String oldCode = d.getDiscountCode();
                    d.setDiscountCode(newCode);
                    d.setDiscountType(typeBox.getValue());
                    d.setAmount(newAmt);
                    d.setDescription(descField.getText().trim());

                    ProductView sel = itemBox.getValue();
                    if (sel != null) {
                        d.setProductId(sel.getProductCode());
                        d.setProductName(sel.getProductName());
                    } else {
                        d.setProductId(0l);
                        d.setProductName(null);
                    }

                    // call the new updateDiscount signature:
                    discountDAO.updateDiscount(d, oldCode);
                }

                loadDiscounts();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Amount", "Please enter a valid number.");
            } catch (SQLException ex) {
                showAlert("DB Error", ex.getMessage());
            }
        }
    }


}
