// File: Batch1_POSG4/view/TotalsView.java
package Batch1_POSG4.view;

import javafx.beans.property.*;

public class TotalsView {
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount      = new SimpleDoubleProperty();

    public TotalsView(String description, double amount) {
        this.description.set(description);
        this.amount     .set(amount);
    }

    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty amountProperty()      { return amount;      }

    public String getDescription() { return description.get(); }
    public double getAmount()      { return amount.get();      }
}
