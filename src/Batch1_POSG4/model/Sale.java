package Batch1_POSG4.model;

import java.sql.Timestamp;

// Represents a sale transaction, including customer, cashier, and payment details.
public class Sale {

    // Instance fields (public)

    // Instance fields (private)
    private final long saleId;
    private final long userId;
    private final Integer customerId;
    private final Timestamp saleDate;
    private final double totalAmount;
    private final String paymentMethod;
    private final String customerName;
    private final String cashier;

    // Constructs a Sale with all details.
    public Sale(long saleId,
                long userId,
                Integer customerId,
                Timestamp saleDate,
                double totalAmount,
                String paymentMethod,
                String customerName,
                String cashier) {
        this.saleId        = saleId;
        this.userId        = userId;
        this.customerId    = customerId;
        this.saleDate      = saleDate;
        this.totalAmount   = totalAmount;
        this.paymentMethod = paymentMethod;
        this.customerName  = customerName;
        this.cashier       = cashier;
    }

    // Returns the sale ID.
    public long getSaleId()          { return saleId; }

    // Returns the user ID (cashier) for the sale.
    public long getUserId()          { return userId; }

    // Returns the customer ID for the sale, or null if none.
    public Integer getCustomerId()   { return customerId; }

    // Returns the timestamp of the sale.
    public Timestamp getSaleDate()   { return saleDate; }

    // Returns the total amount for the sale.
    public double getTotalAmount()   { return totalAmount; }

    // Returns the payment method used for the sale.
    public String getPaymentMethod() { return paymentMethod; }

    // Returns the customer name for the sale.
    public String getCustomerName()  { return customerName; }

    // Returns the cashier's username for the sale.
    public String getCashier()       { return cashier; }
}