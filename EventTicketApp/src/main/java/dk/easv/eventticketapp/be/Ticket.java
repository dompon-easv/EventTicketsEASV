package dk.easv.eventticketapp.be;

public class Ticket {
    private int id;
    private int quantity;
    private int eventId;
    private int ticketTypeId;
    private int consumerId;

    public Ticket(int id, int quantity, int eventId, int ticketTypeId, int consumerId) {
        this.id = id;
        this.quantity = quantity;
        this.eventId = eventId;
        this.ticketTypeId = ticketTypeId;
        this.consumerId = consumerId;
    }

    public Ticket(int quantity, int eventId, int ticketTypeId, int consumerId) {
        this.quantity = quantity;
        this.eventId = eventId;
        this.ticketTypeId = ticketTypeId;
        this.consumerId = consumerId;
    }

    public int getId() { return id; }
    public int getQuantity() { return quantity; }
    public int getEventId() { return eventId; }
    public int getTicketTypeId() { return ticketTypeId; }
    public int getCustomerId() { return consumerId; }
}