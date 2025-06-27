package Batch1_POSG4.controller;

// 1. Java library import
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// 2. JavaFX import
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

// 3. Project import
import Batch1_POSG4.view.SaleItemView;
import Batch1_POSG4.view.TotalsView;
import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.dao.SaleDAO;
import Batch1_POSG4.util.Session;


public class SalesController {
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
    @FXML private Button btnOpenCash;
    @FXML private Button btnPrintReciept;
    @FXML private Button btnReturn;
    @FXML private Button btnSearch;
    @FXML private Button btnVoid;
    @FXML private Button btnProceedPayment;
    @FXML private CheckBox chkPDWSenior;
    @FXML private ComboBox<?> cmbCategory;
    @FXML private Label lblDatTime;
    @FXML private ListView<?> lstViewItems;
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

    

    //private boolean barcodeMode = false;
    //private StringBuilder barcodeBuffer = new StringBuilder();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static boolean barcodeMode;
    private final ObservableList<SaleItemView> saleItems = FXCollections.observableArrayList();
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private long currentSaleId;
    private final ObservableList<TotalsView> totalsData = FXCollections.observableArrayList();
    private final StringBuilder barcodeBuffer = new StringBuilder();
    private Integer selectedCustomerId;  // from your Customer picker
    private String  paymentMethod = "Cash"; // or pulled from the UI
    long currentUserId = Session.get().getCurrentUser().getUserId();

    @FXML
    public void initialize() {
        Timeline clock = new Timeline(
        new KeyFrame(Duration.ZERO, e -> {
            lblDatTime.setText("System Date/Time" + LocalDateTime.now().format(FORMATTER));
        }),
        new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        colProdID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName     .setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPriceQty.setCellValueFactory(item -> {
            double price = item.getValue().getUnitPrice();
            int qty     = item.getValue().getQuantity();
            String txt  = String.format("₱%.2f", price, qty);
            return new ReadOnlyStringWrapper(txt);
        });

        colQuantity .setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal .setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        tblSales.setItems(saleItems);
        colAdjustment  .setCellValueFactory(new PropertyValueFactory<>("description"));
        colTotalAmount .setCellValueFactory(new PropertyValueFactory<>("amount"));
        tblTotals.setItems(totalsData);
        chkPDWSenior.selectedProperty().addListener((obs,oldV,newV)-> updateTotals());

        // handle ENTER on the barcode field
        txtBarcode.setOnAction(evt -> onBarcodeEntered());
        rootVBOX.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_TYPED, this::handleGlobalKeyTyped);
            }
        });

        txtPayment.setDisable(true);
        txtChange.setDisable(true);
        txtBarcode.setDisable(true);

    }

    private void handleGlobalKeyTyped(KeyEvent ev) {
        if (!barcodeMode) return;

        String ch = ev.getCharacter();       // this preserves uppercase
        if (ch == null || ch.isEmpty()) {
            // ignore
        }
        else if ("\r".equals(ch) || "\n".equals(ch)) {
            // ENTER pressed
            String scanned = barcodeBuffer.toString();
            barcodeBuffer.setLength(0);
            txtBarcode.setText(scanned);
            onBarcodeEntered();
            ev.consume();
        }
        else {
            // append exactly what was typed (including uppercase)
            barcodeBuffer.append(ch);
            ev.consume();
        }
    }


    private void updateTotals() {
        // 1) gross = sum of all line subtotals
        double gross = saleItems.stream()
                                .mapToDouble(SaleItemView::getTotalPrice)
                                .sum();

        // 2) discount = any code‐based discounts + senior/PWD
        //    (here I’ll assume a flat 20% off if checked; adjust as needed)
        double discount = 0;
        if (chkPDWSenior.isSelected()) {
            discount += gross * 0.20;
        }
        // + you can add other discounts here, e.g.
        // discount += someDiscountDao.getDiscountAmount(currentSaleId);

        // 3) grand total = gross – discount
        double grand = gross - discount;

        // 4) populate the 3 rows
        totalsData.setAll(
        new TotalsView("Gross Total", gross),
        new TotalsView("Discount",    -discount),   // negative so it subtracts
        new TotalsView("Grand Total",  grand)
        );
    }

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

                long   prodId = rs.getLong("product_id");
                String name   = rs.getString("name");
                double price  = rs.getDouble("price");
                int    stock  = rs.getInt("stock");

                if (stock <= 0) {
                    showAlert("Out of stock", name + " is unavailable.");
                    txtBarcode.clear();
                    return;
                }

                // ── 1) Duplicate check ──
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
                                    // totalPriceProperty() is bound, so it updates automatically
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
                    /*saleItemId*/     -1,
                    /*saleId*/          currentSaleId,
                    /*productId*/       prodId,
                    /*productName*/     name,
                    /*quantity*/        1,
                    /*unitPrice*/       price,
                    /*totalPrice*/      price    // if you bind in the model, this initial value will be overridden
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

    
    private void showAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Scan Error");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
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
        updateTotals();
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

    @FXML
    void handlesBarcode(ActionEvent event) {
        if (barcodeMode == true) {
            barcodeMode = false;
            btnBarcode.setText("F3 Barcode Mode is OFF");
        }
        else {
            barcodeMode = true;
            btnBarcode.setText("F3 Barcode Mode is ON");
        }
    }

    @FXML
    void handlesCustomer(ActionEvent event) {

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
    void handlesOpenCash(ActionEvent event) {
        System.out.println("Cash Opened");
    }

    @FXML
    void handlesPDWSenior(ActionEvent event) {

    }

    @FXML
    void handlesPrintReciept(ActionEvent event) {
        // ——— your existing console “print” code ———
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n===== SALES RECEIPT =====");
        System.out.println("Sale ID:    " + currentSaleId);
        System.out.println("Date/Time:  " + LocalDateTime.now().format(fmt));
        System.out.println("Cashier:    " + Session.get().getCurrentUser().getUsername());
        System.out.println("-------------------------------");
        for (SaleItemView item : saleItems) {
            System.out.printf("%-20s %2d x ₱%.2f = ₱%.2f\n",
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            );
        }
        System.out.println("-------------------------------");
        double gross    = totalsData.get(0).getAmount();
        double discount = -totalsData.get(1).getAmount();
        double grand    = totalsData.get(2).getAmount();
        double paid     = txtPayment.getText().isBlank()
                            ? grand
                            : Double.parseDouble(txtPayment.getText());
        double change   = paid - grand;
        System.out.printf("Gross Total:  ₱%.2f\n", gross);
        System.out.printf("Discount:     ₱%.2f\n", discount);
        System.out.printf("Grand Total:  ₱%.2f\n", grand);
        System.out.printf("Paid:         ₱%.2f\n", paid);
        System.out.printf("Change:       ₱%.2f\n", change);
        System.out.println("===============================\n");

        // ——— now clear & start a new sale ———
        // 1) Reset the form
        saleItems.clear();
        totalsData.clear();
        txtPayment.clear();
        txtChange.clear();
        txtBarcode.clear();

        // 2) Kick off a brand‐new transaction
        //    We can just re‐use your existing handler:
        handlesNewTransaction(null);
    }

    @FXML
    void handlesSearch(ActionEvent event) {

    }

    @FXML
    void handlesVoid(ActionEvent event) {

    }
    @FXML
    void handlesProceedPayment(ActionEvent event) {
        // 1) validate payment & compute change (as you already do) …
        if (totalsData.size() < 3) {
            showAlert("Nothing to pay", "Please scan items first.");
            return;
        }

        double paid      = Double.parseDouble(txtPayment.getText().trim());
        double grandTotal= totalsData.get(2).getAmount();
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
        try {
            SaleDAO saleDao = new SaleDAO(dbUrl);
            // pass null customerId for walk‐in; update later if they choose a customer
            currentSaleId = saleDao.createSale(currentUserId, null, paymentMethod);

            // show it in the UI
            txtTransactionNumber.setText(Long.toString(currentSaleId));

            // clear any leftover lines & totals
            saleItems.clear();
            totalsData.clear();
            txtPayment.clear();
            txtChange.clear();

            // enable scanning and payment now that we have a sale in progress
            
            txtPayment.setDisable(false);
            txtChange.setDisable(false);
            txtBarcode.setDisable(false);
            btnProceedPayment.setDisable(false);
            txtBarcode.setDisable(false);
            btnNewTxn.setDisable(true);  // one sale at a time

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Failed to start new transaction:\n" + ex.getMessage());
        }
    }
}
