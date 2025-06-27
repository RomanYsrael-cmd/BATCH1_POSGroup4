// File: Batch1_POSG4/view/SaleItemView.java
package Batch1_POSG4.view;

import javafx.beans.property.*;

public class SaleItemView {
    private final LongProperty    saleItemId   = new SimpleLongProperty();
    private final LongProperty    saleId       = new SimpleLongProperty();
    private final LongProperty    productId    = new SimpleLongProperty();
    private final StringProperty  productName  = new SimpleStringProperty();
    private final DoubleProperty  unitPrice    = new SimpleDoubleProperty();
    private final IntegerProperty quantity     = new SimpleIntegerProperty();
    private final ReadOnlyDoubleWrapper totalPrice = new ReadOnlyDoubleWrapper();
    private final BooleanProperty selected     = new SimpleBooleanProperty(false);

    /**
     * Default constructor for manual creation (empty fields).
     */
    public SaleItemView() {
        // bind totalPrice to unitPrice * quantity
        this.totalPrice.bind(unitPrice.multiply(quantity));
    }

    /**
     * Full constructor used by DAO to populate all fields.
     */
    public SaleItemView(
            long saleItemId,
            long saleId,
            long productId,
            String productName,
            int quantity,
            double unitPrice,
            double totalPrice
    ) {
        // set backing properties
        this.saleItemId.set(saleItemId);
        this.saleId    .set(saleId);
        this.productId .set(productId);
        this.productName.set(productName);
        this.quantity  .set(quantity);
        this.unitPrice .set(unitPrice);
        // bind computed totalPrice = unitPrice * quantity
        this.totalPrice.bind(this.unitPrice.multiply(this.quantity));
        // (initial totalPrice value will reflect bound value)
    }

    // --- Properties ---
    public LongProperty saleItemIdProperty()     { return saleItemId; }
    public LongProperty saleIdProperty()         { return saleId; }
    public LongProperty productIdProperty()      { return productId; }
    public StringProperty productNameProperty()  { return productName; }
    public DoubleProperty unitPriceProperty()    { return unitPrice; }
    public IntegerProperty quantityProperty()    { return quantity; }
    public ReadOnlyDoubleProperty totalPriceProperty() { return totalPrice.getReadOnlyProperty(); }
    public BooleanProperty selectedProperty()    { return selected; }

    // --- Getters & setters ---
    public long getSaleItemId()      { return saleItemId.get(); }
    public void setSaleItemId(long id) { this.saleItemId.set(id); }

    public long getSaleId()          { return saleId.get(); }
    public void setSaleId(long id)   { this.saleId.set(id); }

    public long getProductId()       { return productId.get(); }
    public void setProductId(long id){ this.productId.set(id); }

    public String getProductName()   { return productName.get(); }
    public void setProductName(String name) { this.productName.set(name); }

    public double getUnitPrice()     { return unitPrice.get(); }
    public void setUnitPrice(double price)  { this.unitPrice.set(price); }

    public int getQuantity()         { return quantity.get(); }
    public void setQuantity(int q)   { this.quantity.set(q); }

    public double getTotalPrice()    { return totalPrice.get(); }

    public boolean isSelected()      { return selected.get(); }
    public void setSelected(boolean sel) { this.selected.set(sel); }
}
