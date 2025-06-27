// File: Batch1_POSG4/model/Sale.java
package Batch1_POSG4.model;

import java.sql.Timestamp;

public class Sale {
    private final long saleId;
    private final long userId;
    private final Integer customerId;
    private final Timestamp saleDate;
    private final double totalAmount;
    private final String paymentMethod;
    private final String customerName;
    private final String cashier;

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

    public long getSaleId()          { return saleId; }
    public long getUserId()          { return userId; }
    public Integer getCustomerId()   { return customerId; }
    public Timestamp getSaleDate()   { return saleDate; }
    public double getTotalAmount()   { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCustomerName()  { return customerName; }
    public String getCashier()       { return cashier; }
}
