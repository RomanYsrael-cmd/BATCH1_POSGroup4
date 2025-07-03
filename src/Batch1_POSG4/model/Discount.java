package Batch1_POSG4.model;

public class Discount {
    private long   discountId;
    private String discountCode;
    private String description;
    private String discountType;
    private double amount;

    // item this code applies to (NULL = general)
    private Long   productId;    
    private String productName;  

    public Discount() { }

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

    // getters & setters…

    public long getDiscountId()        { return discountId; }
    public void setDiscountId(long id) { this.discountId = id; }

    public String getDiscountCode()             { return discountCode; }
    public void   setDiscountCode(String code)  { this.discountCode = code; }

    public String getDescription()              { return description; }
    public void   setDescription(String desc)   { this.description = desc; }

    public String getDiscountType()             { return discountType; }
    public void   setDiscountType(String type)  { this.discountType = type; }

    public double getAmount()                   { return amount; }
    public void   setAmount(double amt)         { this.amount = amt; }

    public Long getProductId()                  { return productId; }
    public void setProductId(long id)           {this.productId = id;}


    public String getProductName()              { return productName; }
    public void   setProductName(String name)   { this.productName = name; }
}
