package dk.easv.eventticketapp.be;

public class IssuedTicket {

    private String customerName;
    private String email;
    private String ticketType;
    private int quantity;
    private double price;

    public IssuedTicket(String customerName, String email, String ticketType, int quantity, double totalPrice) {
        this.customerName = customerName;
        this.email = email;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.price = totalPrice;
    }

    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getTicketType() { return ticketType; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}