package Batch1_POSG4.model;

import java.sql.Timestamp;

// Represents a return record for a sale item, including reason and refund amount.
public class ReturnModel {

    // Instance fields (public)

    // Instance fields (private)
    private final long returnId;
    private final long saleItemId;
    private final Timestamp returnDate;
    private final String reason;
    private final double refundAmount;

    // Constructs a ReturnModel with all fields specified.
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

    // Returns the return ID.
    public long getReturnId()        { return returnId; }

    // Returns the sale item ID associated with this return.
    public long getSaleItemId()      { return saleItemId; }

    // Returns the timestamp of the return.
    public Timestamp getReturnDate() { return returnDate; }

    // Returns the reason for the return.
    public String getReason()        { return reason; }

    // Returns the refund amount for the return.
    public double getRefundAmount()  { return refundAmount; }
}