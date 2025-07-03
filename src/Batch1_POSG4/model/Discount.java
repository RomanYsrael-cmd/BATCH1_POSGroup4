package Batch1_POSG4.model;

// Represents a discount, which may be general or apply to a specific product.
public class Discount {

    // Instance fields (public)

    // Instance fields (private)
    private long   discountId;
    private String discountCode;
    private String description;
    private String discountType;
    private double amount;
    private Long   productId;
    private String productName;

    // Constructs an empty Discount.
    public Discount() { }

    // Constructs a Discount with all fields specified.
    public Discount(long discountId,
                    String discountCode,
                    String description,
                    String discountType,
                    double amount,
                    Long productId,
                    String productName)
    {
        this.discountId   = discountId;
        this.discountCode = discountCode;
        this.description  = description;
        this.discountType = discountType;
        this.amount       = amount;
        this.productId    = productId;
        this.productName  = productName;
    }

    // Returns the discount ID.
    public long getDiscountId()        { return discountId; }

    // Sets the discount ID.
    public void setDiscountId(long id) { this.discountId = id; }

    // Returns the discount code.
    public String getDiscountCode()             { return discountCode; }

    // Sets the discount code.
    public void   setDiscountCode(String code)  { this.discountCode = code; }

    // Returns the discount description.
    public String getDescription()              { return description; }

    // Sets the discount description.
    public void   setDescription(String desc)   { this.description = desc; }

    // Returns the discount type (e.g., percentage or fixed).
    public String getDiscountType()             { return discountType; }

    // Sets the discount type.
    public void   setDiscountType(String type)  { this.discountType = type; }

    // Returns the discount amount.
    public double getAmount()                   { return amount; }

    // Sets the discount amount.
    public void   setAmount(double amt)         { this.amount = amt; }

    // Returns the product ID this discount applies to, or null if general.
    public Long getProductId()                  { return productId; }

    // Sets the product ID this discount applies to.
    public void setProductId(long id)           { this.productId = id; }

    // Returns the product name this discount applies to.
    public String getProductName()              { return productName; }

    // Sets the product name this discount applies to.
    public void   setProductName(String name)   { this.productName = name; }
}