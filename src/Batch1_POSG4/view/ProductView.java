package Batch1_POSG4.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProductView {
    private final SimpleIntegerProperty productCode;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty category;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty barcode;

    public ProductView(int productCode, String productName, String category, int quantity, double price, String barcode) {
        this.productCode = new SimpleIntegerProperty(productCode);
        this.productName = new SimpleStringProperty(productName);
        this.category = new SimpleStringProperty(category);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.barcode = new SimpleStringProperty(barcode);
    }

    public int getProductCode() { return productCode.get(); }
    public String getProductName() { return productName.get(); }
    public String getCategory() { return category.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }
    public String getBarcode() { return barcode.get(); }

    public SimpleIntegerProperty productCodeProperty() { return productCode; }
    public SimpleStringProperty productNameProperty() { return productName; }
    public SimpleStringProperty categoryProperty() { return category; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public SimpleDoubleProperty priceProperty() { return price; }
    public SimpleStringProperty barcodeProperty() { return barcode; }
    @Override
    public String toString() {
        return String.format("%s (₱%.2f)", getProductName(), getPrice());
    }
}