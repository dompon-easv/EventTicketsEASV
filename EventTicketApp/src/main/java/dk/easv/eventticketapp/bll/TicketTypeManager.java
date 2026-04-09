package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.dao.ITicketTypeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TicketTypeManager {

    private final ITicketTypeDAO ticketTypeDAO;
    private Event currentEvent;
    private final EventLogic eventLogic;

    public TicketTypeManager(ITicketTypeDAO ticketTypeDAO) {
        this.ticketTypeDAO = ticketTypeDAO;
        this.eventLogic = new EventLogic();
    }

    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public int getTotalTicketQuantityForEvent(int eventId) throws Exception {
        List<TicketType> ticketTypes = ticketTypeDAO.getTicketTypesForEvent(eventId);
        int total = 0;
        for (TicketType ticketType : ticketTypes) {
            total += ticketType.getMaxQuantity();
        }
        return total;
    }

    private void validateCapacityLimit(int eventId, int newQuantity, Integer excludeTicketTypeId) throws Exception {
        int currentTotal = getTotalTicketQuantityForEvent(eventId);

        if (excludeTicketTypeId != null) {
            TicketType existing = ticketTypeDAO.getById(excludeTicketTypeId);
            if (existing != null) {
                currentTotal -= existing.getMaxQuantity();
            }
        }

        int newTotal = currentTotal + newQuantity;

        Event event = getEventById(eventId);
        int maxCapacity = event.getMaxTickets();

        if (newTotal > maxCapacity) {
            int available = maxCapacity - currentTotal;
            throw new IllegalArgumentException(String.format(
                    "Cannot save ticket type!\n\n" +
                            "Event Capacity: %d tickets\n" +
                            "Current ticket types total: %d tickets\n" +
                            "Available capacity: %d tickets\n" +
                            "You're trying to add: %d tickets\n" +
                            "New total would be: %d tickets\n\n" +
                            "Please reduce the total ticket quantity or increase the event capacity.",
                    maxCapacity,
                    currentTotal,
                    available,
                    newQuantity,
                    newTotal,
                    available
            ));
        }
    }

    private Event getEventById(int eventId) throws Exception {
        List<Event> events = eventLogic.getAllEvents();
        for (Event event : events) {
            if (event.getId() == eventId) {
                return event;
            }
        }
        throw new Exception("Event not found with ID: " + eventId);
    }

    public void addTicketType(String name, String description, double price, int quantity) throws Exception {
        validateEventSelected();
        validateTicketType(name, description, price, quantity);
        validateCapacityLimit(currentEvent.getId(), quantity, null);

        String normalizedName = normalizeName(name);
        if (ticketTypeDAO.existsByNameAndEvent(normalizedName, currentEvent.getId())) {
            throw new IllegalArgumentException("A ticket type with this name already exists for this event.");
        }

        TicketType ticketType = new TicketType(
                0, // id placeholder
                name,
                description,
                price,
                currentEvent.getId(),
                quantity
        );

        ticketTypeDAO.add(ticketType);
    }

    public void updateTicketType(TicketType ticketType) throws Exception {
        if (ticketType.getId() <= 0) {
            throw new IllegalArgumentException("Invalid ticket type ID");
        }

        validateTicketType(
                ticketType.getName(),
                ticketType.getDescription(),
                ticketType.getPrice(),
                ticketType.getMaxQuantity()
        );
        validateCapacityLimit(ticketType.getEventId(), ticketType.getMaxQuantity(), ticketType.getId());

        String normalizedName = normalizeName(ticketType.getName());
        if (ticketTypeDAO.existsByNameAndEventExcludingId(
                normalizedName,
                ticketType.getEventId(),
                ticketType.getId())) {
            throw new IllegalArgumentException("A ticket type with this name already exists for this event.");
        }

        ticketTypeDAO.update(ticketType);
    }

    public void deleteTicketType(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ticket type ID");
        }

        TicketType ticketType = ticketTypeDAO.getById(id);
        if (ticketType == null) {
            throw new Exception("Ticket type not found");
        }

        int ticketCount = ticketTypeDAO.getTicketCountForTicketType(id);
        if (ticketCount > 0) {
            throw new Exception(String.format(
                    "Cannot delete ticket type '%s' because %d ticket(s) have already been sold for this type.\n\n" +
                            "You can only delete ticket types that have no tickets issued.",
                    ticketType.getName(), ticketCount
            ));
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

        if (quantity == 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    public ObservableList<TicketType> getTicketTypesForEvent(int eventId) throws Exception {
        List<TicketType> ticketTypes = ticketTypeDAO.getTicketTypesForEvent(eventId);
        return FXCollections.observableArrayList(ticketTypes);
    }

    public TicketType getTicketTypeById(int id) throws Exception {
        return ticketTypeDAO.getById(id);
    }

    private String normalizeName(String name) {
        return name.replaceAll("\\s+", "").toLowerCase();
    }

    public void deleteEvent(int eventId) {
        try {
            ticketTypeDAO.deleteEvent(eventId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTotalCapacityValid(int eventId) throws Exception {
        int total = getTotalTicketQuantityForEvent(eventId);
        Event event = getEventById(eventId);
        return total <= event.getMaxTickets();
    }

    public String getCapacitySummary(int eventId) throws Exception {
        int total = getTotalTicketQuantityForEvent(eventId);
        Event event = getEventById(eventId);
        int maxCapacity = event.getMaxTickets();
        int available = maxCapacity - total;

        return String.format("Total: %d / %d tickets (Available: %d)", total, maxCapacity, available);
    }
}