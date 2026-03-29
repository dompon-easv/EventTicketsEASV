package dk.easv.eventticketapp.be;

public class TicketType {
    private int id;
    private String name;
    private double price;
    private int eventId;
    private int quantityAvailable;

    public TicketType(int id, String name, double price, int eventId, int quantityAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.eventId = eventId;
        this.quantityAvailable = quantityAvailable;
    }

    public TicketType(String name, double price, int eventId, int quantityAvailable) {
        this.name = name;
        this.price = price;
        this.eventId = eventId;
        this.quantityAvailable = quantityAvailable;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    public int getEventId() {return eventId;}
    public void setEventId(int eventId) {this.eventId = eventId;}

    public int getQuantityAvailable() {return quantityAvailable;}
    public void setQuantityAvailable(int quantityAvailable) {this.quantityAvailable = quantityAvailable;}

}