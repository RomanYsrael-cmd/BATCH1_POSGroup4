package Batch1_POSG4.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// Represents a product for display in inventory and sales views, using JavaFX properties.
public class ProductView {

    // Instance fields (public)

    // Instance fields (private)
    private final SimpleIntegerProperty productCode;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty category;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty barcode;

    // Constructs a ProductView with all display fields.
    public ProductView(int productCode, String productName, String category, int quantity, double price, String barcode) {
        this.productCode = new SimpleIntegerProperty(productCode);
        this.productName = new SimpleStringProperty(productName);
        this.category = new SimpleStringProperty(category);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.barcode = new SimpleStringProperty(barcode);
    }

    // Returns the product code.
    public int getProductCode() { return productCode.get(); }

    // Returns the product name.
    public String getProductName() { return productName.get(); }

    // Returns the product category.
    public String getCategory() { return category.get(); }

    // Returns the product quantity.
    public int getQuantity() { return quantity.get(); }

    // Returns the product price.
    public double getPrice() { return price.get(); }

    // Returns the product barcode.
    public String getBarcode() { return barcode.get(); }

    // Returns the JavaFX property for product code.
    public SimpleIntegerProperty productCodeProperty() { return productCode; }

    // Returns the JavaFX property for product name.
    public SimpleStringProperty productNameProperty() { return productName; }

    // Returns the JavaFX property for category.
    public SimpleStringProperty categoryProperty() { return category; }

    // Returns the JavaFX property for quantity.
    public SimpleIntegerProperty quantityProperty() { return quantity; }

    // Returns the JavaFX property for price.
    public SimpleDoubleProperty priceProperty() { return price; }

    // Returns the JavaFX property for barcode.
    public SimpleStringProperty barcodeProperty() { return barcode; }

    // Returns a string representation for display in UI controls.
    @Override
    public String toString() {
        return String.format("%s (₱%.2f)", getProductName(), getPrice());
    }
}