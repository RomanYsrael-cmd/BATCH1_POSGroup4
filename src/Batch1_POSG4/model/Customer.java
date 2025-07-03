package Batch1_POSG4.model;

// Represents a customer with ID, name, email, phone, and loyalty points.
public class Customer {

    // Instance fields (public)

    // Instance fields (private)
    private final Integer customerId;
    private final String  name;
    private final String  email;
    private final String  phone;
    private final Integer loyaltyPoints;

    // Constructs a Customer with the given details.
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

    // Returns the customer ID.
    public Integer getCustomerId()    { return customerId; }

    // Returns the customer's name.
    public String  getName()          { return name; }

    // Returns the customer's email.
    public String  getEmail()         { return email; }

    // Returns the customer's phone number.
    public String  getPhone()         { return phone; }

    // Returns the customer's loyalty points.
    public Integer getLoyaltyPoints() { return loyaltyPoints; }

    // Returns a string representation of the customer.
    @Override
    public String toString() {
        return name + " (" + customerId + ")";
    }
}