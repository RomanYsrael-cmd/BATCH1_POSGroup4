package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Batch1_POSG4.dao.InventoryAddDAO;
import Batch1_POSG4.dao.ProductDAO;
import Batch1_POSG4.util.Session;
import Batch1_POSG4.view.ProductView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class InventoryController  {

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

    private static final int ROWS_PER_PAGE = 10;
    private int currentPageIndex = 0;
    private ObservableList<ProductView> pageData;
    long currentUserId = Session.get().getCurrentUser().getUserId();

    @FXML
    public void initialize() {
        // 1) set up columns
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        // 2) load & wrap data
        masterData   = new ProductDAO().fetchInventoryWithCategory();
        filteredData = new FilteredList<>(masterData, pv -> true);
        tblInventory.setItems(filteredData);
        // 3) build the “Category” combo with ALL + distinct values
        ObservableList<String> cats = FXCollections.observableArrayList();
        
        cats.add("All Categories");
        masterData.stream()
                .map(ProductView::getCategory)
                .distinct()
                .sorted()
                .forEach(cats::add);

        cmbCategory.setItems(cats);
        cmbCategory.getSelectionModel().selectFirst(); //all categories yung default 
        cmbCategory.setOnAction(e -> filterByCategory());

        // 4) SEARCH BY
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

        // SEARCH
        txtSearch.textProperty().addListener((obs,o,n) -> applySearchFilter());
        cmbFilter.setOnAction(e -> applySearchFilter());
    

    }
    private void showInventoryDialog(ProductView item) {
        // create controls
        TextField qtyField = new TextField();
        qtyField.setPromptText("Enter quantity");
        Button btnSet = new Button("Set Inventory");
        Button btnAdd = new Button("Add Inventory");

        // layout
        HBox buttons = new HBox(10, btnSet, btnAdd);
        buttons.setAlignment(Pos.CENTER);
        VBox root = new VBox(10,
            new Label("Product: " + item.getProductName()),
            new Label("Current qty: " + item.getQuantity()),
            new HBox(5, new Label("Quantity:"), qtyField),
            buttons
        );
        root.setPadding(new Insets(15));

        // dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Inventory");
        dialog.setScene(new Scene(root));
        dialog.initOwner(tblInventory.getScene().getWindow());

        // DAO (use your same DB URL)
        String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
        InventoryAddDAO dao = new InventoryAddDAO(dbUrl);

        // common handler
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
                refreshInventoryTable();  // reload data so the table updates
            } catch (SQLException ex) {
                new Alert(AlertType.ERROR, "DB error: " + ex.getMessage()).showAndWait();
            }
        };
        btnSet.setOnAction(handler);
        btnAdd.setOnAction(handler);

        dialog.showAndWait();
    }

    private void applySearchFilter() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String searchBy = cmbFilter.getSelectionModel().getSelectedItem();
        String selectedCategory = cmbCategory.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(pv -> {
            // 1) Category filter: if not “All Categories” and doesn’t match, exclude
            if (!"All Categories".equals(selectedCategory) 
                && !pv.getCategory().equals(selectedCategory)) {
                return false;
            }

            // 2) Text search: if empty, include all remaining
            if (searchText.isEmpty()) {
                return true;
            }

            // 3) Otherwise, test the chosen field
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

    private void filterByCategory() {
        String selected = cmbCategory.getSelectionModel().getSelectedItem();
        filteredData.setPredicate(pv -> {
            // “All” shows everything
            if ("All Categories".equals(selected)) {
                return true;
            }
            // otherwise only rows whose category equals the selection
            return pv.getCategory().equals(selected);
        });
    }


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

    //This line of code is unused
    private void updatePage() {
        int total = filteredData.size();
        int pages = (int)Math.ceil((double)total/ROWS_PER_PAGE);
        int from  = currentPageIndex * ROWS_PER_PAGE;
        int to    = Math.min(from + ROWS_PER_PAGE, total);

        // refill the page buffer from the newly-filtered list
        pageData.setAll(filteredData.subList(from, to));

        lblPageNumber.setText("Page " + (currentPageIndex+1) + " of " + pages);
        btnPrevPage.setDisable(currentPageIndex == 0);
        btnNextPage.setDisable(currentPageIndex >= pages-1);
    }

    @FXML
    private void handleSearch(KeyEvent event) {

    }

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        // filter by category
    }

    @FXML
    private void handleOtherFilter(ActionEvent event) {
        // filter by other options
    }

    @FXML
    private void handleAddItem(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSAddInventory.fxml"));
        Parent addInvRoot = loader.load();
        AddInventoryController addCtrl = loader.getController();
        addCtrl.loadCategories();  // still load the drop-down in the dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.setScene(new Scene(addInvRoot));
        dialog.setTitle("Add Inventory");
        dialog.showAndWait();   
        refreshInventoryTable();
    }

    @FXML
    private void handlePrevPage(ActionEvent event) {
        // load previous page
        if (currentPageIndex > 0) {
        currentPageIndex--;
        updatePage();
    }

    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        int totalPages = (int) Math.ceil((double) masterData.size() / ROWS_PER_PAGE);
        if (currentPageIndex < totalPages - 1) {
            currentPageIndex++;
            updatePage();
        }
    }
    
    @FXML
    void handlesSearch(ActionEvent event) {

    }
    
}