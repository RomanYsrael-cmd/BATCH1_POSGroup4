package Batch1_POSG4.model;

public class MonthlyReport {
    private final String period;   // e.g. "June 2025"
    private final double total;    // sum of total_amount

    public MonthlyReport(String period, double total) {
        this.period = period;
        this.total  = total;
    }

    public String getPeriod() { return period; }
    public double getTotal()  { return total; }
}
