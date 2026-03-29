package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.dao.ITicketTypeDAO;
import dk.easv.eventticketapp.dao.TicketTypeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TicketTypeManager {

    private final ITicketTypeDAO ticketTypeDAO;
    private Event currentEvent; // This is an instance variable, not static

    // Constructor with dependency injection
    public TicketTypeManager(ITicketTypeDAO ticketTypeDAO) {
        this.ticketTypeDAO = ticketTypeDAO;
    }

    // Default constructor using TicketTypeDAO
    public TicketTypeManager() {
        this.ticketTypeDAO = new TicketTypeDAO();
    }

    // This is an INSTANCE method, not static
    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    // This is an INSTANCE method, not static
    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void addTicketType(String name, double price, int quantity) throws Exception {
        if (currentEvent == null) {
            throw new Exception("No event selected to associate ticket type with.");
        }

        // Business validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        TicketType ticketType = new TicketType(
                0,                     // id placeholder
                name,
                price,
                currentEvent.getId(),
                quantity
        );

        ticketTypeDAO.add(ticketType);
    }

    public void addTicketType(String name, double price, int quantity, Event event) throws Exception {
        setCurrentEvent(event);
        addTicketType(name, price, quantity);
    }

    public void updateTicketType(TicketType ticketType) throws Exception {
        if (ticketType.getId() <= 0) {
            throw new IllegalArgumentException("Invalid ticket type ID");
        }
        ticketTypeDAO.update(ticketType);
    }

    public void deleteTicketType(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ticket type ID");
        }
        ticketTypeDAO.delete(id);
    }

    public ObservableList<TicketType> getTicketTypesForEvent(int eventId) throws Exception {
        List<TicketType> ticketTypes = ticketTypeDAO.getTicketTypesForEvent(eventId);
        return FXCollections.observableArrayList(ticketTypes);
    }

    public TicketType getTicketTypeById(int id) throws Exception {
        return ticketTypeDAO.getById(id);
    }
}