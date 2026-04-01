package dk.easv.eventticketapp.be;

public class IssuedTicket {

    private String customerName;
    private String email;
    private String ticketType;
    private int quantity;

    public IssuedTicket(String customerName, String email, String ticketType, int quantity) {
        this.customerName = customerName;
        this.email = email;
        this.ticketType = ticketType;
        this.quantity = quantity;
    }

    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getTicketType() { return ticketType; }
    public int getQuantity() { return quantity; }
}