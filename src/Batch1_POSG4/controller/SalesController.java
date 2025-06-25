package Batch1_POSG4.controller;

import java.beans.EventHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import Batch1_POSG4.view.SaleItemView;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML private TableView<?> tblTotals;
    @FXML private TextField txtBarcode;
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtDiscount;
    @FXML private TextField txtSearchItem;
    @FXML private TextField txtTransactionNumber;
    
    //private boolean barcodeMode = false;
    //private StringBuilder barcodeBuffer = new StringBuilder();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static boolean barcodeMode;
    private final ObservableList<SaleItemView> saleItems = FXCollections.observableArrayList();
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private long currentSaleId;

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

        // handle ENTER on the barcode field
        txtBarcode.setOnAction(evt -> onBarcodeEntered());
    
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
                    return;
                }

                long   prodId = rs.getLong("product_id");
                String name   = rs.getString("name");
                double price  = rs.getDouble("price");
                int    stock  = rs.getInt("stock");

                if (stock <= 0) {
                    showAlert("Out of stock", name + " is unavailable.");
                    return;
                }

                // ── 1) Duplicate check ──
                for (SaleItemView item : saleItems) {
                    if (item.getProductId() == prodId) {
                        // Prompt the user to enter a new quantity
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
                                } else {
                                    item.quantityProperty().set(newQty);
                                    // totalPriceProperty() is bound, so it updates automatically
                                    tblSales.refresh();
                                }
                            } catch (NumberFormatException ex) {
                                showAlert("Invalid input", "Please enter a valid integer.");
                            }
                        }
                        txtBarcode.clear();
                        return;
                    }
                }

                // ── 2) New line-item if not a duplicate ──
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

    }

    @FXML
    void handlesPDWSenior(ActionEvent event) {

    }

    @FXML
    void handlesPrintReciept(ActionEvent event) {

    }

    @FXML
    void handlesSearch(ActionEvent event) {

    }

    @FXML
    void handlesVoid(ActionEvent event) {

    }

}
