package Batch1_POSG4.model;

/**
 * Simple POJO for a customer row.
 */
public class Customer {
    private final Integer customerId;
    private final String  name;
    private final String  email;
    private final String  phone;
    private final Integer loyaltyPoints;

    public Customer(Integer customerId,
                    String name,
                    String email,
                    String phone,
                    Integer loyaltyPoints)
    {
        this.customerId    = customerId;
        this.name          = name;
        this.email         = email;
        this.phone         = phone;
        this.loyaltyPoints = loyaltyPoints;
    }

    public Integer getCustomerId()    { return customerId; }
    public String  getName()          { return name; }
    public String  getEmail()         { return email; }
    public String  getPhone()         { return phone; }
    public Integer getLoyaltyPoints() { return loyaltyPoints; }

    @Override
    public String toString() {
        // so that if you ever print a Customer, you see something useful:
        return name + " (" + customerId + ")";
    }
}
