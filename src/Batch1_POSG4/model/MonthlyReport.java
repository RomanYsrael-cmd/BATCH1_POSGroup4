package Batch1_POSG4.model;

// Represents a monthly sales report with period and total sales amount.
public class MonthlyReport {

    // Instance fields (public)

    // Instance fields (private)
    private final String period;
    private final double total;

    // Constructs a MonthlyReport with the given period and total sales.
    public MonthlyReport(String period, double total) {
        this.period = period;
        this.total  = total;
    }

    // Returns the period string (e.g., "June 2025").
    public String getPeriod() { return period; }

    // Returns the total sales amount for the period.
    public double getTotal()  { return total; }
}