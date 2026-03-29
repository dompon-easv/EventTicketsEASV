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
    private Event currentEvent;

    public TicketTypeManager(ITicketTypeDAO ticketTypeDAO) {
        this.ticketTypeDAO = ticketTypeDAO;
    }

    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public TicketType addTicketType(String name, String description, double price, int quantity) throws Exception {
        validateEventSelected();
        validateTicketType(name, description, price, quantity);

        TicketType ticketType = new TicketType(
                0, // id placeholder
                name,
                description,
                price,
                currentEvent.getId(),
                quantity
        );

        ticketTypeDAO.add(ticketType);
        return ticketType;
    }

    public void updateTicketType(TicketType ticketType) throws Exception {
        if (ticketType.getId() <= 0) {
            throw new IllegalArgumentException("Invalid ticket type ID");
        }
        validateTicketType(ticketType.getName(), ticketType.getDescription(), ticketType.getPrice(), ticketType.getQuantityAvailable());
        ticketTypeDAO.update(ticketType);
    }

    public void deleteTicketType(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ticket type ID");
        }
        ticketTypeDAO.delete(id);
    }

    public void validateEventSelected() throws Exception {
        if (currentEvent == null) {
            throw new Exception("No event selected to associate ticket type with.");
        }
    }

    public void validateTicketType(String name, String description, double price, int quantity) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket name cannot be empty");
        }

        if (description != null && description.length() > 100) {
            throw new IllegalArgumentException("Description cannot exceed 100 characters");
        }

        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    public ObservableList<TicketType> getTicketTypesForEvent(int eventId) throws Exception {
        List<TicketType> ticketTypes = ticketTypeDAO.getTicketTypesForEvent(eventId);
        return FXCollections.observableArrayList(ticketTypes);
    }

    public TicketType getTicketTypeById(int id) throws Exception {
        return ticketTypeDAO.getById(id);
    }
}