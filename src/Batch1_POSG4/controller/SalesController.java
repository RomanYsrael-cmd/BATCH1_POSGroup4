package Batch1_POSG4.controller;

// Standard library imports
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

// Third-party packages
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

// Project-specific imports
import Batch1_POSG4.dao.CustomerDAO;
import Batch1_POSG4.dao.DiscountDAO;
import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.dao.ProductDAO;
import Batch1_POSG4.dao.ProductDAO.Category;
import Batch1_POSG4.dao.SaleDAO;
import Batch1_POSG4.model.Customer;
import Batch1_POSG4.model.Discount;
import Batch1_POSG4.util.PdfReceiptGenerator;
import Batch1_POSG4.util.Session;
import Batch1_POSG4.view.ProductView;
import Batch1_POSG4.view.SaleItemView;
import Batch1_POSG4.view.TotalsView;

// Controls the main POS sales screen, handling product selection, transactions, and payment.
public class SalesController {

    // Public constants

    // Private constants
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Public static fields
    public static boolean barcodeMode;
    public static boolean discountApplied;

    // Private static fields

    // Public instance fields

    // Private instance fields
    @FXML private VBox rootVBOX;
    @FXML private ToggleButton btnBarcode;
    @FXML private Button btnCategory;
    @FXML private Button btnCustomers;
    @FXML private Button btnDiscount;
    @FXML private Button btnEditQuantity;
    @FXML private Button btnItem1;
    @FXML private Button btnItem2;
    @FXML private Button btnItem3;
    @FXML private Button btnItem4;
    @FXML private Button btnItem5;
    @FXML private Button btnItem6;
    @FXML private Button btnItem7;
    @FXML private Button btnItem8;
    @FXML private Button btnItem9;
    @FXML private Button btnManageDiscount;
    @FXML private ToggleButton btnApplyDiscount;    
    @FXML private Button btnOpenCash;
    @FXML private Button btnPrintReciept;
    @FXML private Button btnReturn;
    @FXML private Button btnSearch;
    @FXML private Button btnVoid;
    @FXML private Button btnProceedPayment;
    @FXML private CheckBox chkPDWSenior;
    @FXML private ComboBox<Category> cmbCategory;
    @FXML private Label lblDatTime;
    @FXML private ListView<ProductView> lstViewItems;
    @FXML private TableView<SaleItemView> tblSales;
    @FXML private TableColumn<SaleItemView, Long> colProdID;
    @FXML private TableColumn<SaleItemView,String> colName;
    @FXML private TableColumn<SaleItemView,String> colPriceQty;
    @FXML private TableColumn<SaleItemView,Integer> colQuantity;
    @FXML private TableColumn<SaleItemView,Double> colSubtotal;
    @FXML private TableView<TotalsView> tblTotals;
    @FXML private TableColumn<TotalsView,String> colAdjustment;
    @FXML private TableColumn<TotalsView,Double> colTotalAmount;
    @FXML private TextField txtBarcode;
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtDiscount;
    @FXML private TextField txtSearchItem;
    @FXML private TextField txtTransactionNumber;
    @FXML private TextField txtPayment;
    @FXML private TextField txtChange;
    @FXML private Button btnNewTxn;
    @FXML private ComboBox<String> cmbSearchBy;
    @FXML private ComboBox<ProductDAO.Category> cmbCategories;
    private final ObservableList<SaleItemView> saleItems = FXCollections.observableArrayList();
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private long currentSaleId;
    private final ObservableList<TotalsView> totalsData = FXCollections.observableArrayList();
    private final StringBuilder barcodeBuffer = new StringBuilder();
    private Integer selectedCustomerId;
    private String paymentMethod = "Cash";
    long currentUserId = Session.get().getCurrentUser().getUserId();
    private String lastCode;     
    private boolean lastDidPWD;

    // Initializes the sales controller and sets up UI bindings and event handlers.
    @FXML
    public void initialize() {
        // Set up the system clock label to update every second.
        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                lblDatTime.setText("System Date/Time" + LocalDateTime.now().format(FORMATTER));
            }),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // Set up table columns for sales items.
        colProdID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPriceQty.setCellValueFactory(item -> {
            double price = item.getValue().getUnitPrice();
            int qty = item.getValue().getQuantity();
            String txt = String.format("₱%.2f", price, qty);
            return new ReadOnlyStringWrapper(txt);
        });
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        tblSales.setItems(saleItems);

        // Set up table columns for totals.
        colAdjustment.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tblTotals.setItems(totalsData);

        // Set up barcode field to handle ENTER key.
        txtBarcode.setOnAction(evt -> onBarcodeEntered());

        // Add global key event filter for barcode mode.
        rootVBOX.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_TYPED, this::handleGlobalKeyTyped);
            }
        });

        // Disable controls initially.
        disableControls();

        // Set up search by options.
        cmbSearchBy.getItems().setAll("Name", "Barcode");
        cmbSearchBy.getSelectionModel().selectFirst();

        // Load categories into combo box.
        ObservableList<Category> cats = new ProductDAO().fetchAllCategories();
        cats.add(0, new Category(null, "All Categories"));
        cmbCategories.setItems(cats);
        cmbCategories.getSelectionModel().selectFirst();

        // Add listeners for search and category selection.
        txtSearchItem.textProperty().addListener((o, a, b) -> refreshProducts());
        cmbSearchBy.setOnAction(e -> refreshProducts());
        cmbCategories.setOnAction(e -> refreshProducts());

        // Load initial product list.
        refreshProducts();

        // Set up keyboard shortcuts for various actions.
        Platform.runLater(() -> {
            Scene scene = btnOpenCash.getScene();
            if (scene == null) return;
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F1),
                () -> btnOpenCash.fire()
            );
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F3),
                () -> btnBarcode.fire()
            );
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F6),
                () -> btnDiscount.fire()
            );
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F7),
                () -> btnVoid.fire()
            );
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F9),
                () -> btnReturn.fire()
            );
        });

        // Set up payment field to accept only valid numeric input.
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txtPayment.setTextFormatter(formatter);

        // Set up discount button to apply or remove discounts.
        btnApplyDiscount.setOnAction(evt -> {
            if (btnApplyDiscount.isSelected()) {
                applyDiscountCode();
            } else {
                totalsData.removeIf(tv ->
                    tv.getDescription().equals("PDW/Senior 20%") ||
                    (lastCode != null && tv.getDescription().equals("Discount (" + lastCode + ")"))
                );
                lastCode = null;
                lastDidPWD = false;
                recalcGrandTotalRow();
            }
        });

        // Set up double-click on sales table to edit quantity.
        tblSales.setRowFactory(tv -> {
            TableRow<SaleItemView> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    SaleItemView item = row.getItem();
                    TextInputDialog dlg = new TextInputDialog(
                        Integer.toString(item.getQuantity())
                    );
                    dlg.setTitle("Edit Quantity");
                    dlg.setHeaderText("Adjust quantity for “" + item.getProductName() + "”");
                    dlg.setContentText("Enter a value (0 to remove):");
                    Optional<String> result = dlg.showAndWait();
                    if (result.isPresent()) {
                        try {
                            int newQty = Integer.parseInt(result.get().trim());
                            if (newQty < 0) {
                                showAlert("Invalid Quantity", "Please enter 0 or a positive integer.");
                            } else if (newQty == 0) {
                                saleItems.remove(item);
                            } else {
                                item.quantityProperty().set(newQty);
                            }
                            updateTotals();
                            tblSales.refresh();
                        } catch (NumberFormatException ex) {
                            showAlert("Invalid Input", "Please enter a whole number.");
                        }
                    }
                }
            });
            return row;
        });

        // Disable print receipt button initially.
        btnPrintReciept.setDisable(true);
    }

    // Refreshes the product list based on search and category filters.
    private void refreshProducts() {
        String filter = txtSearchItem.getText().trim();
        String searchBy = cmbSearchBy.getValue();
        Category cat = cmbCategories.getValue();
        Integer catId = (cat == null ? null : cat.getCategoryId());
        try {
            ObservableList<ProductView> items = new ProductDAO().search(filter, searchBy, catId);
            lstViewItems.setItems(items);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Handles global key events for barcode scanning mode.
    private void handleGlobalKeyTyped(KeyEvent ev) {
        if (!barcodeMode) return;
        String ch = ev.getCharacter();
        if (ch == null || ch.isEmpty()) {
        } else if ("\r".equals(ch) || "\n".equals(ch)) {
            String scanned = barcodeBuffer.toString();
            barcodeBuffer.setLength(0);
            txtBarcode.setText(scanned);
            onBarcodeEntered();
            ev.consume();
        } else {
            barcodeBuffer.append(ch);
            ev.consume();
        }
    }

    // Updates the totals table with gross and grand total.
    private void updateTotals() {
        double gross = saleItems.stream()
            .mapToDouble(SaleItemView::getTotalPrice)
            .sum();
        double grand = gross;
        totalsData.setAll(
            new TotalsView("Gross Total", gross),
            new TotalsView("Grand Total", grand)
        );
    }

    // Handles barcode entry and adds the corresponding product to the sale.
    @FXML
    private void onBarcodeEntered() {
        String code = txtBarcode.getText().trim();
        if (code.isEmpty()) return;
        final String sql = """
            SELECT p.product_id, p.name, p.price, i.quantity AS stock
            FROM tbl_Product p
            JOIN tbl_Inventory i ON p.product_id = i.product_id
            WHERE p.barcode = ?
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    showAlert("Product not found", "No product with barcode: " + code);
                    txtBarcode.clear();
                    return;
                }
                long prodId = rs.getLong("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                if (stock <= 0) {
                    showAlert("Out of stock", name + " is unavailable.");
                    txtBarcode.clear();
                    return;
                }
                for (SaleItemView item : saleItems) {
                    if (item.getProductId() == prodId) {
                        TextInputDialog dialog = new TextInputDialog(
                            Integer.toString(item.getQuantity())
                        );
                        dialog.setTitle("Edit Quantity");
                        dialog.setHeaderText("Adjust quantity for " + name);
                        dialog.setContentText("Enter a value between 1 and " + stock + ":");
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            try {
                                int newQty = Integer.parseInt(result.get());
                                if (newQty < 1 || newQty > stock) {
                                    showAlert("Invalid quantity",
                                        "Please enter a whole number from 1 to " + stock);
                                    updateTotals();
                                } else {
                                    item.quantityProperty().set(newQty);
                                    tblSales.refresh();
                                    updateTotals();
                                }
                            } catch (NumberFormatException ex) {
                                showAlert("Invalid input", "Please enter a valid integer.");
                            }
                        }
                        txtBarcode.clear();
                        updateTotals();
                        return;
                    }
                }
                SaleItemView line = new SaleItemView(
                    -1,
                    currentSaleId,
                    prodId,
                    name,
                    1,
                    price,
                    price
                );
                saleItems.add(line);
                updateTotals();
                txtBarcode.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    // Displays an alert dialog with the given header and body.
    private void showAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Scan Error");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    // Opens the discount manager dialog.
    @FXML
    void handlesDiscount(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/Batch1_POSG4/view/POSDiscount.fxml")
        );
        Parent root = loader.load();
        DiscountController dc = loader.getController();
        dc.setSaleId(currentSaleId);
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(root));
        dialog.setTitle("Discount Manager");
        dialog.showAndWait();
        updateTotals();
    }

    // Opens the returns manager dialog.
    @FXML
    void handlesReturn(ActionEvent event) throws IOException {
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

    // Closes the sales screen and returns to the main menu.
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

    // Toggles barcode scanning mode on or off.
    @FXML
    void handlesBarcode(ActionEvent event) {
        if (barcodeMode == true) {
            barcodeMode = false;
            btnBarcode.setText("F3 Barcode Mode is OFF");
        } else {
            barcodeMode = true;
            btnBarcode.setText("F3 Barcode Mode is ON");
        }
    }

    // Opens the customer selection dialog and updates the selected customer.
    @FXML
    void handlesCustomer(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/Batch1_POSG4/view/POSCustomer.fxml"
        ));
        Parent dialogRoot = loader.load();
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(dialogRoot));
        dialog.setTitle("Select Customer");
        dialog.showAndWait();
        CustomerController cc = loader.getController();
        Customer chosen = cc.getSelectedCustomer();
        if (chosen != null) {
            this.selectedCustomerId = chosen.getCustomerId();
            txtCustomerName.setText(chosen.getName());
        }
    }

    // Handles editing the quantity of a selected sale item (not implemented).
    @FXML
    void handlesEditQuantity(ActionEvent event) {
    }

    // Adds the first item from the product list to the sale.
    @FXML void handlesItem1(ActionEvent e) { addFromList(0); }
    // Adds the second item from the product list to the sale.
    @FXML void handlesItem2(ActionEvent e) { addFromList(1); }
    // Adds the third item from the product list to the sale.
    @FXML void handlesItem3(ActionEvent e) { addFromList(2); }
    // Adds the fourth item from the product list to the sale.
    @FXML void handlesItem4(ActionEvent e) { addFromList(3); }
    // Adds the fifth item from the product list to the sale.
    @FXML void handlesItem5(ActionEvent e) { addFromList(4); }
    // Adds the sixth item from the product list to the sale.
    @FXML void handlesItem6(ActionEvent e) { addFromList(5); }
    // Adds the seventh item from the product list to the sale.
    @FXML void handlesItem7(ActionEvent e) { addFromList(6); }
    // Adds the eighth item from the product list to the sale.
    @FXML void handlesItem8(ActionEvent e) { addFromList(7); }
    // Adds the ninth item from the product list to the sale.
    @FXML void handlesItem9(ActionEvent e) { addFromList(8); }

    // Adds a product from the product list at the specified index to the sale.
    private void addFromList(int idx) {
        ObservableList<ProductView> items = lstViewItems.getItems();
        if (idx < 0 || idx >= items.size()) {
            return;
        }
        ProductView p = items.get(idx);
        if (p.getQuantity() <= 0) {
            showAlert("Out of Stock", p.getProductName() + " is unavailable.");
            return;
        }
        for (SaleItemView line : saleItems) {
            if (line.getProductId() == p.getProductCode()) {
                int newQty = Math.min(p.getQuantity(), line.getQuantity() + 1);
                line.quantityProperty().set(newQty);
                tblSales.refresh();
                updateTotals();
                return;
            }
        }
        saleItems.add(new SaleItemView(
            -1,
            currentSaleId,
            p.getProductCode(),
            p.getProductName(),
            1,
            p.getPrice(),
            p.getPrice()
        ));
        updateTotals();
    }

    // Handles opening the cash drawer (stub).
    @FXML
    void handlesOpenCash(ActionEvent event) {
        System.out.println("Cash Opened");
    }

    // Handles toggling PWD/Senior discount (not implemented).
    @FXML
    void handlesPDWSenior(ActionEvent event) {
    }

    // Handles printing the receipt and resetting the UI for a new transaction.
    @FXML
    void handlesPrintReciept(ActionEvent e) {
        if (btnPrintReciept.isDisabled()) {
            return;
        }
        try {
            Path out = Path.of(
                System.getProperty("user.home"),
                "Documents",
                "receipt_" + currentSaleId + ".pdf"
            );
            List<String> lines = saleItems.stream()
                .map(it -> String.format("%-18s %2d x PHP %.2f = PHP %.2f",
                        it.getProductName(),
                        it.getQuantity(),
                        it.getUnitPrice(),
                        it.getTotalPrice()))
                .toList();
            double gross = findGrossTotal();
            double grand = findGrandTotal();
            double discount = gross - grand;
            double paid = txtPayment.getText().isBlank()
                ? grand
                : Double.parseDouble(txtPayment.getText());
            double change = paid - grand;
            PdfReceiptGenerator.write(out,
                currentSaleId,
                Session.get().getCurrentUser().getUsername(),
                lines, gross, discount, grand, paid, change);
            java.awt.Desktop.getDesktop().open(out.toFile());
            saleItems.clear(); totalsData.clear();
            txtPayment.clear(); txtChange.clear(); txtBarcode.clear();
            handlesNewTransaction(null);
            btnPrintReciept.setDisable(true);
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("PDF Error", "Could not create receipt:\n" + ex.getMessage());
        }
    }

    // Handles searching for products (not implemented).
    @FXML
    void handlesSearch(ActionEvent event) {
    }

    // Handles voiding the current transaction and starting a new one.
    @FXML
    void handlesVoid(ActionEvent event) {
        saleItems.clear();
        totalsData.clear();
        txtPayment.clear();
        txtChange.clear();
        btnApplyDiscount.setDisable(false);
        chkPDWSenior.setDisable(false);
        try {
            SaleDAO saleDao = new SaleDAO(dbUrl);
            currentSaleId = saleDao.createSale(currentUserId, null, paymentMethod);
            txtTransactionNumber.setText(Long.toString(currentSaleId));
            updateTotals();
            enableControls();
            btnNewTxn.setDisable(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Failed to start new transaction:\n" + ex.getMessage());
        }
    }

    // Handles proceeding to payment, validating input, and saving the sale.
    @FXML
    void handlesProceedPayment(ActionEvent event) {
        if (saleItems.size() < 1) {
            showAlert("Nothing to pay", "Please scan items first.");
            return;
        }
        if (txtPayment.getLength() == 0) {
            showAlert("No payment set", "Paayment error.");
            return;
        }
        double paid = Double.parseDouble(txtPayment.getText().trim());
        double grandTotal = findGrandTotal();
        if (paid < grandTotal) {
            showAlert("Insufficient Payment", "Payment error");
            return;
        }
        txtChange.setText(String.format("PHP %.2f", paid - grandTotal));
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            conn.setAutoCommit(false);
            String insertSQL = """
            INSERT INTO tbl_SaleItem(sale_id, product_id, quantity, unit_price, total_price)
            VALUES (?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                for (SaleItemView item : saleItems) {
                    ps.setLong(1, currentSaleId);
                    ps.setLong(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getUnitPrice());
                    ps.setDouble(5, item.getTotalPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            String updSQL = """
            UPDATE tbl_Sale
                SET total_amount   = ?,
                    payment_method = ?
            WHERE sale_id = ?
            """;
            try (PreparedStatement ps2 = conn.prepareStatement(updSQL)) {
                ps2.setDouble(1, grandTotal);
                ps2.setString(2, paymentMethod);
                ps2.setLong(3, currentSaleId);
                ps2.executeUpdate();
            }
            InventoryAddDAO invDao = new InventoryAddDAO(dbUrl);
            for (SaleItemView item : saleItems) {
                invDao.adjustInventory(conn, item.getProductId(), -item.getQuantity());
            }
            conn.commit();
            if (selectedCustomerId != null) {
                int pointsEarned = (int) (grandTotal / 100);
                try {
                    CustomerDAO custDao = new CustomerDAO(dbUrl);
                    int newTotal = custDao.addLoyaltyPoints(selectedCustomerId, pointsEarned);
                    showAlert("Loyalty Points Awarded",
                        "Awarded " +
                        pointsEarned +
                        " points, your new total is " +
                        newTotal
                    );
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Loyalty Error",
                        "Sale completed, but failed to update loyalty points:\n"
                        + ex.getMessage());
                }
            }
            showAlert("Payment Complete",
                String.format("Transaction #%d OK. Change: PHP %.2f",
                    currentSaleId, paid - grandTotal));
            disableControls();
            btnProceedPayment.setDisable(true);
            txtBarcode.setDisable(true);
            btnPrintReciept.setDisable(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Database Error", ex.getMessage());
        }
    }

    // Starts a new transaction and resets the UI.
    @FXML
    void handlesNewTransaction(ActionEvent event) {
        try {
            SaleDAO saleDao = new SaleDAO(dbUrl);
            currentSaleId = saleDao.createSale(currentUserId, null, paymentMethod);
            txtTransactionNumber.setText(Long.toString(currentSaleId));
            saleItems.clear();
            updateTotals();
            txtPayment.clear();
            txtChange.clear();
            enableControls();
            btnNewTxn.setDisable(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Failed to start new transaction:\n" + ex.getMessage());
        }
    }

    // Enables controls for a new transaction.
    @FXML
    void enableControls() {
        btnItem1.setDisable(false);
        btnItem2.setDisable(false);
        btnItem3.setDisable(false);
        btnItem4.setDisable(false);
        btnItem5.setDisable(false);
        btnItem6.setDisable(false);
        btnItem7.setDisable(false);
        btnItem8.setDisable(false);
        btnItem9.setDisable(false);
        txtPayment.setDisable(false);
        txtBarcode.setDisable(false);
        txtDiscount.setDisable(false);
        txtSearchItem.setDisable(false);
        btnProceedPayment.setDisable(false);
        btnApplyDiscount.setDisable(false);
    }

    // Disables controls to prevent further input.
    @FXML
    void disableControls() {
        btnItem1.setDisable(true);
        btnItem2.setDisable(true);
        btnItem3.setDisable(true);
        btnItem4.setDisable(true);
        btnItem5.setDisable(true);
        btnItem6.setDisable(true);
        btnItem7.setDisable(true);
        btnItem8.setDisable(true);
        btnItem9.setDisable(true);
        txtPayment.setDisable(true);
        txtBarcode.setDisable(true);
        txtDiscount.setDisable(true);
        txtSearchItem.setDisable(true);
        btnProceedPayment.setDisable(true);
        btnApplyDiscount.setDisable(true);
    }

    // Handles applying a discount code (not implemented).
    @FXML
    void handlesApplyDiscount(ActionEvent event) {
    }

    // Applies a discount code to the current sale and updates totals.
    private void applyDiscountCode() {
        String code = txtDiscount.getText().trim();
        if (code.isEmpty()) {
            showAlert("Missing Code", "Please enter a discount code.");
            return;
        }
        try {
            DiscountDAO dao = new DiscountDAO(dbUrl);
            Optional<Discount> opt = dao.findByCode(code);
            if (opt.isEmpty()) {
                showAlert("Invalid Discount", "No such discount code.");
                return;
            }
            Discount d = opt.get();
            double discValue;
            Long pid = d.getProductId();
            if (pid != null) {
                var lineOpt = saleItems.stream()
                    .filter(si -> si.getProductId() == pid)
                    .findFirst();
                if (lineOpt.isEmpty()) {
                    showAlert("Not Applicable",
                        "Discount “" + code + "” only applies to “"
                        + d.getProductName() + "”.");
                    return;
                }
                SaleItemView line = lineOpt.get();
                double unit = line.getUnitPrice();
                if (d.getDiscountType().equalsIgnoreCase("Percentage")) {
                    discValue = unit * d.getAmount() / 100.0;
                } else {
                    discValue = Math.min(d.getAmount(), unit);
                }
            } else {
                double gross = saleItems.stream()
                    .mapToDouble(SaleItemView::getTotalPrice)
                    .sum();
                if ("Percentage".equalsIgnoreCase(d.getDiscountType())) {
                    discValue = gross * d.getAmount() / 100.0;
                } else {
                    discValue = d.getAmount();
                }
            }
            totalsData.add(new TotalsView("Discount (" + code + ")", -discValue));
            lastCode = code;
            if (chkPDWSenior.isSelected()) {
                double gross = saleItems.stream().mapToDouble(SaleItemView::getTotalPrice).sum();
                double pwdValue = gross * 0.20;
                totalsData.add(new TotalsView("PDW/Senior 20%", -pwdValue));
                lastDidPWD = true;
            } else {
                lastDidPWD = false;
            }
            recalcGrandTotalRow();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Database Error", ex.getMessage());
        }
    }

    // Recalculates the grand total row in the totals table.
    private void recalcGrandTotalRow() {
        double gross = totalsData.get(0).getAmount();
        double adjustments = totalsData.stream()
            .filter(tv -> !tv.getDescription().equals("Gross Total")
                && !tv.getDescription().equals("Grand Total"))
            .mapToDouble(TotalsView::getAmount)
            .sum();
        double grand = gross + adjustments;
        totalsData.removeIf(tv -> tv.getDescription().equals("Grand Total"));
        totalsData.add(new TotalsView("Grand Total", grand));
    }

    // Finds and returns the gross total from the totals table.
    private double findGrossTotal() {
        return totalsData.stream()
            .filter(tv -> tv.getDescription().equals("Gross Total"))
            .findFirst()
            .map(TotalsView::getAmount)
            .orElse(0.0);
    }

    // Finds and returns the grand total from the totals table.
    private double findGrandTotal() {
        return totalsData.stream()
            .filter(tv -> tv.getDescription().equals("Grand Total"))
            .findFirst()
            .map(TotalsView::getAmount)
            .orElse(0.0);
    }
}