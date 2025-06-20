package Batch1_POSG4.model;

import java.sql.Timestamp;

public class ReturnModel {
    private final long returnId;
    private final long saleItemId;
    private final Timestamp returnDate;
    private final String reason;
    private final double refundAmount;

    public ReturnModel(long returnId,
                  long saleItemId,
                  Timestamp returnDate,
                  String reason,
                  double refundAmount) {
        this.returnId     = returnId;
        this.saleItemId   = saleItemId;
        this.returnDate   = returnDate;
        this.reason       = reason;
        this.refundAmount = refundAmount;
    }

    public long getReturnId()     { return returnId; }
    public long getSaleItemId()   { return saleItemId; }
    public Timestamp getReturnDate() { return returnDate; }
    public String getReason()     { return reason; }
    public double getRefundAmount() { return refundAmount; }
}
