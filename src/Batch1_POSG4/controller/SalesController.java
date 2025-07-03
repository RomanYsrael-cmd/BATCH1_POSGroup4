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
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.nio.file.Path;

// 2. JavaFX import
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
import Batch1_POSG4.view.ProductView;

// 3. Project import
import Batch1_POSG4.view.SaleItemView;
import Batch1_POSG4.view.TotalsView;
import Batch1_POSG4.dao.CustomerDAO;
import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.dao.ProductDAO;
import Batch1_POSG4.dao.ProductDAO.Category;

import Batch1_POSG4.dao.SaleDAO;
import Batch1_POSG4.model.Customer;
import Batch1_POSG4.util.PdfReceiptGenerator;
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
    @FXML private ComboBox<String>        cmbSearchBy;
    @FXML private ComboBox<ProductDAO.Category> cmbCategories;
    //private boolean barcodeMode = false;
    //private StringBuilder barcodeBuffer = new StringBuilder();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static boolean barcodeMode;
    private final ObservableList<SaleItemView> saleItems = FXCollections.observableArrayList();
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private long currentSaleId;
    private final ObservableList<TotalsView> totalsData = FXCollections.observableArrayList();
    private final StringBuilder barcodeBuffer = new StringBuilder();
    private Integer selectedCustomerId;
    private String  paymentMethod = "Cash";
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

        disableControls();
            
        cmbSearchBy.getItems().setAll("Name","Barcode");
        cmbSearchBy.getSelectionModel().selectFirst();

        // 2) categories
        ObservableList<Category> cats = new ProductDAO().fetchAllCategories();
        cats.add(0, new Category(null,"All Categories"));
        cmbCategories.setItems(cats);
        cmbCategories.getSelectionModel().selectFirst();

        // 3) listeners
        txtSearchItem.textProperty().addListener((o,a,b)-> refreshProducts());
        cmbSearchBy.setOnAction(e-> refreshProducts());
        cmbCategories.setOnAction(e-> refreshProducts());

        // 4) initial load
        refreshProducts();
            Platform.runLater(() -> {
        Scene scene = btnOpenCash.getScene();
        if (scene == null) return;

        // F1 → Open Cash
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.F1),
            () -> btnOpenCash.fire()
        );

        // F3 → Toggle Barcode Mode
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.F3),
            () -> btnBarcode.fire()
        );

        // F6 → Discount
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.F6),
            () -> btnDiscount.fire()
        );

        // F7 → Void
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.F7),
            () -> btnVoid.fire()
        );

        // F9 → Returns
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.F9),
            () -> btnReturn.fire()
        );
    });
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // regex: any number of digits, optionally one “.” plus any number of digits
            if (newText.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null; // reject change
        };
        
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txtPayment.setTextFormatter(formatter);

    }
    private void refreshProducts() {
        String filter   = txtSearchItem.getText().trim();
        String searchBy = cmbSearchBy.getValue();
        Category cat    = cmbCategories.getValue();
        Integer catId   = (cat == null ? null : cat.getCategoryId());

        try {
            ObservableList<ProductView> items = new ProductDAO().search(filter, searchBy, catId);
            lstViewItems.setItems(items);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
        double gross = saleItems.stream().mapToDouble(SaleItemView::getTotalPrice).sum();

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
    void handlesCustomer(ActionEvent event) throws IOException {
        // a) load the FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
        "/Batch1_POSG4/view/POSCustomer.fxml"
        ));
        Parent dialogRoot = loader.load();

        // b) show as modal dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(dialogRoot));
        dialog.setTitle("Select Customer");
        dialog.showAndWait();

        // c) after close, retrieve selection
        CustomerController cc = loader.getController();
        Customer chosen = cc.getSelectedCustomer();
        if (chosen != null) {
            // store for use on payment
            this.selectedCustomerId = chosen.getCustomerId();

            // show in UI
            txtCustomerName.setText(chosen.getName());
        }
    }

    @FXML
    void handlesEditQuantity(ActionEvent event) {

    }

    @FXML void handlesItem1(ActionEvent e){ addFromList(0); }
    @FXML void handlesItem2(ActionEvent e){ addFromList(1); }
    @FXML void handlesItem3(ActionEvent e){ addFromList(2); }
    @FXML void handlesItem4(ActionEvent e){ addFromList(3); }
    @FXML void handlesItem5(ActionEvent e){ addFromList(4); }
    @FXML void handlesItem6(ActionEvent e){ addFromList(5); }
    @FXML void handlesItem7(ActionEvent e){ addFromList(6); }
    @FXML void handlesItem8(ActionEvent e){ addFromList(7); }
    @FXML void handlesItem9(ActionEvent e){ addFromList(8); }
    
    private void addFromList(int idx) {
        // 1) pull the current items out of the ListView
        ObservableList<ProductView> items = lstViewItems.getItems();

        // 2) guard against out-of-bounds
        if (idx < 0 || idx >= items.size()) {
            return;
        }

        ProductView p = items.get(idx);

        // 3) check stock (your "quantity" column in ProductView)
        if (p.getQuantity() <= 0) {
            showAlert("Out of Stock", p.getProductName() + " is unavailable.");
            return;
        }

        // 4) duplicate check
        for (SaleItemView line : saleItems) {
            if (line.getProductId() == p.getProductCode()) {
                // bump quantity by 1, up to available stock
                int newQty = Math.min(p.getQuantity(), line.getQuantity() + 1);
                line.quantityProperty().set(newQty);
                tblSales.refresh();
                updateTotals();
                return;
            }
        }

        // 5) otherwise add a brand-new row
        saleItems.add(new SaleItemView(
            /* saleItemId: */   -1,
            /* saleId: */        currentSaleId,
            /* productId: */     p.getProductCode(),
            /* productName: */   p.getProductName(),
            /* quantity: */      1,
            /* unitPrice: */     p.getPrice(),
            /* totalPrice: */    p.getPrice()
        ));
        updateTotals();
    }


    @FXML
    void handlesOpenCash(ActionEvent event) {
        System.out.println("Cash Opened");
    }

    @FXML
    void handlesPDWSenior(ActionEvent event) {

    }
    @FXML
    void handlesPrintReciept(ActionEvent e) {
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

            double gross    = totalsData.get(0).getAmount();
            double discount = -totalsData.get(1).getAmount();
            double grand    = totalsData.get(2).getAmount();
            double paid     = txtPayment.getText().isBlank()
                                ? grand
                                : Double.parseDouble(txtPayment.getText());
            double change   = paid - grand;

            PdfReceiptGenerator.write(out,
                    currentSaleId,
                    Session.get().getCurrentUser().getUsername(),
                    lines, gross, discount, grand, paid, change);

            java.awt.Desktop.getDesktop().open(out.toFile());

            // reset UI—same as before
            saleItems.clear(); totalsData.clear();
            txtPayment.clear(); txtChange.clear(); txtBarcode.clear();
            handlesNewTransaction(null);

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("PDF Error", "Could not create receipt:\n" + ex.getMessage());
        }
    }


    @FXML
    void handlesSearch(ActionEvent event) {
        
    }

    @FXML
    void handlesVoid(ActionEvent event) {
        saleItems.clear();
        totalsData.clear();
        txtPayment.clear();
        txtChange.clear();
    }

    @FXML
    void handlesProceedPayment(ActionEvent event) {
        // 1) validate payment & compute change (as you already do) …
        if (totalsData.size() < 3) {
            showAlert("Nothing to pay", "Please scan items first.");
            return;
        }

        if (txtPayment.getLength() == 0){
            showAlert("No payment set", "Paayment error.");
            return;
        }

        double paid      = Double.parseDouble(txtPayment.getText().trim());
        double grandTotal= totalsData.get(2).getAmount();
        if (paid < grandTotal) {
        showAlert("Insufficient Payment", "Payment error");
        return;
        }
        txtChange.setText(String.format("PHP %.2f", paid - grandTotal));

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
            currentSaleId = saleDao.createSale(currentUserId, null, paymentMethod);

            txtTransactionNumber.setText(Long.toString(currentSaleId));

            saleItems.clear();
            totalsData.clear();
            txtPayment.clear();
            txtChange.clear();
            
            enableControls();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB Error", "Failed to start new transaction:\n" + ex.getMessage());
        }
    }
    
    @FXML
    void enableControls(){
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
        txtSearchItem.setDisable(false);;
        btnProceedPayment.setDisable(false);
    }

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
    }
}
