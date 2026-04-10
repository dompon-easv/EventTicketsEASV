package dk.easv.eventticketapp.be;

public class IssuedTicket {

    private int id;
    private String customerName;
    private String email;
    private String ticketType;
    private int quantity;
    private double price;

    public IssuedTicket(int id, String customerName, String email, String ticketType, int quantity, double totalPrice) {
        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.price = totalPrice;
    }

    public int getId() { return id;}
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getTicketType() { return ticketType; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}