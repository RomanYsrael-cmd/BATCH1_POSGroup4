package Batch1_POSG4.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Represents a total line (e.g., subtotal, tax, grand total) for display in sales and reports.
public class TotalsView {

    // Instance fields (public)

    // Instance fields (private)
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount      = new SimpleDoubleProperty();

    // Constructs a TotalsView with a description and amount.
    public TotalsView(String description, double amount) {
        this.description.set(description);
        this.amount     .set(amount);
    }

    // Returns the JavaFX property for description.
    public StringProperty descriptionProperty() { return description; }

    // Returns the JavaFX property for amount.
    public DoubleProperty amountProperty()      { return amount;      }

    // Returns the description string.
    public String getDescription() { return description.get(); }

    // Returns the amount value.
    public double getAmount()      { return amount.get();      }
}