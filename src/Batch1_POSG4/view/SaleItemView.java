package Batch1_POSG4.view;

import javafx.beans.property.*;

// Represents a sale item for display and editing in sales views, using JavaFX properties.
public class SaleItemView {

    // Instance fields (public)

    // Instance fields (private)
    private final LongProperty    saleItemId   = new SimpleLongProperty();
    private final LongProperty    saleId       = new SimpleLongProperty();
    private final LongProperty    productId    = new SimpleLongProperty();
    private final StringProperty  productName  = new SimpleStringProperty();
    private final DoubleProperty  unitPrice    = new SimpleDoubleProperty();
    private final IntegerProperty quantity     = new SimpleIntegerProperty();
    private final ReadOnlyDoubleWrapper totalPrice = new ReadOnlyDoubleWrapper();
    private final BooleanProperty selected     = new SimpleBooleanProperty(false);

    // Construct an empty SaleItemView for manual creation.
    public SaleItemView() {
        this.totalPrice.bind(unitPrice.multiply(quantity));
    }

    // Construct a SaleItemView with all fields populated.
    public SaleItemView(
            long saleItemId,
            long saleId,
            long productId,
            String productName,
            int quantity,
            double unitPrice,
            double totalPrice
    ) {
        this.saleItemId.set(saleItemId);
        this.saleId    .set(saleId);
        this.productId .set(productId);
        this.productName.set(productName);
        this.quantity  .set(quantity);
        this.unitPrice .set(unitPrice);
        this.totalPrice.bind(this.unitPrice.multiply(this.quantity));
    }

    // Returns the JavaFX property for sale item ID.
    public LongProperty saleItemIdProperty()     { return saleItemId; }

    // Returns the JavaFX property for sale ID.
    public LongProperty saleIdProperty()         { return saleId; }

    // Returns the JavaFX property for product ID.
    public LongProperty productIdProperty()      { return productId; }

    // Returns the JavaFX property for product name.
    public StringProperty productNameProperty()  { return productName; }

    // Returns the JavaFX property for unit price.
    public DoubleProperty unitPriceProperty()    { return unitPrice; }

    // Returns the JavaFX property for quantity.
    public IntegerProperty quantityProperty()    { return quantity; }

    // Returns the JavaFX property for total price (read-only).
    public ReadOnlyDoubleProperty totalPriceProperty() { return totalPrice.getReadOnlyProperty(); }

    // Returns the JavaFX property for selection state.
    public BooleanProperty selectedProperty()    { return selected; }

    // Returns the sale item ID.
    public long getSaleItemId()      { return saleItemId.get(); }

    // Sets the sale item ID.
    public void setSaleItemId(long id) { this.saleItemId.set(id); }

    // Returns the sale ID.
    public long getSaleId()          { return saleId.get(); }

    // Sets the sale ID.
    public void setSaleId(long id)   { this.saleId.set(id); }

    // Returns the product ID.
    public long getProductId()       { return productId.get(); }

    // Sets the product ID.
    public void setProductId(long id){ this.productId.set(id); }

    // Returns the product name.
    public String getProductName()   { return productName.get(); }

    // Sets the product name.
    public void setProductName(String name) { this.productName.set(name); }

    // Returns the unit price.
    public double getUnitPrice()     { return unitPrice.get(); }

    // Sets the unit price.
    public void setUnitPrice(double price)  { this.unitPrice.set(price); }

    // Returns the quantity.
    public int getQuantity()         { return quantity.get(); }

    // Sets the quantity.
    public void setQuantity(int q)   { this.quantity.set(q); }

    // Returns the total price (computed).
    public double getTotalPrice()    { return totalPrice.get(); }

    // Returns whether this item is selected.
    public boolean isSelected()      { return selected.get(); }

    // Sets the selection state.
    public void setSelected(boolean sel) { this.selected.set(sel); }
}