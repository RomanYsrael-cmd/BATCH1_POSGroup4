package Batch1_POSG4.controller;

// Standard library imports
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Third-party packages
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Project-specific imports
import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.dao.ProductDAO;
import Batch1_POSG4.util.Session;
import Batch1_POSG4.view.ProductView;

// Manages inventory operations, including viewing, searching, filtering, and updating product inventory.
public class InventoryController  {

    // Public constants

    // Private constants
    private static final int ROWS_PER_PAGE = 10;

    // Public static fields

    // Private static fields

    // Public instance fields

    // Private instance fields
    @FXML private Button btnAddItem;
    @FXML private Button btnNextPage;
    @FXML private Button btnPrevPage;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private TextField txtSearch;
    @FXML public TableView<ProductView> tblInventory;
    @FXML public TableColumn<ProductView, Integer> colProductCode;
    @FXML public TableColumn<ProductView, String> colProductName;
    @FXML public TableColumn<ProductView, String> colCategory;
    @FXML public TableColumn<ProductView, Integer> colQuantity;
    @FXML public TableColumn<ProductView, Double> colPrice;
    @FXML public TableColumn<ProductView, String> colBarcode;
    @FXML private FilteredList<ProductView> filteredData;
    @FXML private ObservableList<ProductView> masterData;
    @FXML private Label lblPageNumber; 

    private int currentPageIndex = 0;
    private ObservableList<ProductView> pageData;
    long currentUserId = Session.get().getCurrentUser().getUserId();

    // Initializes the inventory controller, sets up table columns, loads data, and configures event handlers.
    @FXML
    public void initialize() {
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        masterData   = new ProductDAO().fetchInventoryWithCategory();
        filteredData = new FilteredList<>(masterData, pv -> true);
        tblInventory.setItems(filteredData);

        ObservableList<String> cats = FXCollections.observableArrayList();
        cats.add("All Categories");
        masterData.stream()
                .map(ProductView::getCategory)
                .distinct()
                .sorted()
                .forEach(cats::add);

        cmbCategory.setItems(cats);
        cmbCategory.getSelectionModel().selectFirst();
        cmbCategory.setOnAction(e -> filterByCategory());

        cmbFilter.getItems().addAll("Barcode", "Product Code", "Product Name");
        cmbFilter.getSelectionModel().selectFirst();

        tblInventory.setRowFactory(tv -> {
            TableRow<ProductView> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getButton() == MouseButton.PRIMARY
                    && evt.getClickCount() == 2
                    && !row.isEmpty()) {
                    ProductView item = row.getItem();
                    showInventoryDialog(item);
                }
            });
            return row;
        });

        txtSearch.textProperty().addListener((obs,o,n) -> applySearchFilter());
        cmbFilter.setOnAction(e -> applySearchFilter());

        tblInventory.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                ProductView selected = tblInventory.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert confirm = new Alert(AlertType.CONFIRMATION,
                        "Are you sure you want to delete \"" + selected.getProductName() + "\"?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            try {
                                new InventoryAddDAO("jdbc:sqlite:db/db_pos_g4.db")
                                    .deleteInventory(selected.getProductCode());
                                refreshInventoryTable();
                            } catch (SQLException ex) {
                                new Alert(AlertType.ERROR, "DB error: " + ex.getMessage())
                                    .showAndWait();
                            }
                        }
                    });
                }
            }
        });
    }

    // Shows a dialog for updating or adding inventory for the selected product.
    private void showInventoryDialog(ProductView item) {
        TextField qtyField = new TextField();
        qtyField.setPromptText("Enter quantity");
        Button btnSet = new Button("Set Inventory");
        Button btnAdd = new Button("Add Inventory");

        HBox buttons = new HBox(10, btnSet, btnAdd);
        buttons.setAlignment(Pos.CENTER);
        VBox root = new VBox(10,
            new Label("Product: " + item.getProductName()),
            new Label("Current qty: " + item.getQuantity()),
            new HBox(5, new Label("Quantity:"), qtyField),
            buttons
        );
        root.setPadding(new Insets(15));

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Inventory");
        dialog.setScene(new Scene(root));
        dialog.initOwner(tblInventory.getScene().getWindow());

        String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
        InventoryAddDAO dao = new InventoryAddDAO(dbUrl);

        EventHandler<ActionEvent> handler = e -> {
            String txt = qtyField.getText().trim();
            int q;
            try {
                q = Integer.parseInt(txt);
            } catch (NumberFormatException ex) {
                new Alert(AlertType.ERROR, "Please enter a valid integer.").showAndWait();
                return;
            }
            try {
                if (e.getSource() == btnSet) {
                    dao.setInventory(item.getProductCode(), q);
                } else {
                    dao.adjustInventory(item.getProductCode(), q);
                }
                dialog.close();
                refreshInventoryTable();
            } catch (SQLException ex) {
                new Alert(AlertType.ERROR, "DB error: " + ex.getMessage()).showAndWait();
            }
        };
        btnSet.setOnAction(handler);
        btnAdd.setOnAction(handler);

        dialog.showAndWait();
    }

    // Applies the search and filter criteria to the inventory table.
    private void applySearchFilter() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String searchBy = cmbFilter.getSelectionModel().getSelectedItem();
        String selectedCategory = cmbCategory.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(pv -> {
            if (!"All Categories".equals(selectedCategory) 
                && !pv.getCategory().equals(selectedCategory)) {
                return false;
            }
            if (searchText.isEmpty()) {
                return true;
            }
            switch (searchBy) {
                case "Barcode":
                    return pv.getBarcode().toLowerCase().contains(searchText);
                case "Product Code":
                    return String.valueOf(pv.getProductCode()).contains(searchText);
                case "Product Name":
                    return pv.getProductName().toLowerCase().contains(searchText);
                default:
                    return true;
            }
        });
    }

    // Filters the inventory table by the selected category.
    private void filterByCategory() {
        String selected = cmbCategory.getSelectionModel().getSelectedItem();
        filteredData.setPredicate(pv -> {
            if ("All Categories".equals(selected)) {
                return true;
            }
            return pv.getCategory().equals(selected);
        });
    }

    // Establishes a connection to the SQLite database.
    private Connection connect() {
        String url = "jdbc:sqlite:db/db_pos_g4.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // Loads all categories from the database and populates the category combo box.
    public void loadCategories() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT name FROM tbl_Category";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
            cmbCategory.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles closing the inventory screen and returning to the main menu.
    public void handleClose(Stage inventoryStage, MainMenuController caller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
            Parent mainMenu = loader.load();
            Stage mainMenuStage = new Stage();
            mainMenuStage.setScene(new Scene(mainMenu));
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();
            inventoryStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Refreshes the inventory table and reloads categories.
    @FXML
    private void refreshInventoryTable() {
        masterData = new ProductDAO().fetchInventoryWithCategory();
        filteredData = new FilteredList<>(masterData, pv -> true);
        tblInventory.setItems(filteredData);
        ObservableList<String> cats = FXCollections.observableArrayList("All Categories");
        masterData.stream().map(ProductView::getCategory)
            .distinct()
            .sorted()
            .forEach(cats::add);
        cmbCategory.setItems(cats);
        cmbCategory.getSelectionModel().selectFirst();
    }

    // Updates the page view for paginated inventory display.
    private void updatePage() {
        int total = filteredData.size();
        int pages = (int)Math.ceil((double)total/ROWS_PER_PAGE);
        int from  = currentPageIndex * ROWS_PER_PAGE;
        int to    = Math.min(from + ROWS_PER_PAGE, total);

        pageData.setAll(filteredData.subList(from, to));

        lblPageNumber.setText("Page " + (currentPageIndex+1) + " of " + pages);
        btnPrevPage.setDisable(currentPageIndex == 0);
        btnNextPage.setDisable(currentPageIndex >= pages-1);
    }

    // Handles search key events (currently not implemented).
    @FXML
    private void handleSearch(KeyEvent event) {
    }

    // Handles category filter action (currently not implemented).
    @FXML
    private void handleCategoryFilter(ActionEvent event) {
    }

    // Handles other filter action (currently not implemented).
    @FXML
    private void handleOtherFilter(ActionEvent event) {
    }

    // Opens the add inventory dialog and refreshes the table after closing.
    @FXML
    private void handleAddItem(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSAddInventory.fxml"));
        Parent addInvRoot = loader.load();
        AddInventoryController addCtrl = loader.getController();
        addCtrl.loadCategories();
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(addInvRoot));
        dialog.setTitle("Add Inventory");
        dialog.showAndWait();   
        refreshInventoryTable();
    }

    // Loads the previous page of inventory items.
    @FXML
    private void handlePrevPage(ActionEvent event) {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            updatePage();
        }
    }

    // Loads the next page of inventory items.
    @FXML
    private void handleNextPage(ActionEvent event) {
        int totalPages = (int) Math.ceil((double) masterData.size() / ROWS_PER_PAGE);
        if (currentPageIndex < totalPages - 1) {
            currentPageIndex++;
            updatePage();
        }
    }
    
    // Handles search action (currently not implemented).
    @FXML
    void handlesSearch(ActionEvent event) {
    }
}