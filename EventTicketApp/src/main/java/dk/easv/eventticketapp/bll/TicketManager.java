package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Ticket;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.dao.ICustomerDAO;
import dk.easv.eventticketapp.dao.IEventDAO;
import dk.easv.eventticketapp.dao.ITicketDAO;
import dk.easv.eventticketapp.dao.ITicketTypeDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketManager {

    private final ITicketDAO ticketDAO;
    private final ITicketTypeDAO ticketTypeDAO;
    private final ICustomerDAO customerDAO;
    private final IEventDAO eventDAO;

    private static final int MAX_TICKETS_PER_CUSTOMER = 5;

    public TicketManager(ITicketDAO ticketDAO, ITicketTypeDAO ticketTypeDAO, ICustomerDAO customerDAO, IEventDAO eventDAO) {
        this.ticketDAO = ticketDAO;
        this.ticketTypeDAO = ticketTypeDAO;
        this.customerDAO = customerDAO;
        this.eventDAO = eventDAO;
    }

    public void issueTicket(int quantity, int eventId, int ticketTypeId, int customerId) throws Exception {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        TicketType ticketType = ticketTypeDAO.getById(ticketTypeId);
        if (ticketType == null) {
            throw new IllegalArgumentException("Ticket type not found");
        }

        int totalSold = ticketDAO.getTotalSoldForTicketType(ticketTypeId);

        if (totalSold + quantity > ticketType.getMaxQuantity()) {
            throw new IllegalArgumentException("Not enough tickets available. Only "
                    + (ticketType.getMaxQuantity() - totalSold) + " left.");
        }

        int alreadyBought = ticketDAO.getTotalTicketsByCustomer(customerId, eventId);
        if (alreadyBought + quantity > MAX_TICKETS_PER_CUSTOMER) {
            throw new IllegalArgumentException(
                    "This customer has already bought " + alreadyBought +
                            " tickets. Maximum allowed is " + MAX_TICKETS_PER_CUSTOMER + "."
            );
        }

        String barcode = generateBarcodeValue(customerId, eventId);

        Ticket ticket = new Ticket(quantity, eventId, ticketTypeId, customerId);
        ticket.setBarcode(barcode);
        ticketDAO.add(ticket);
    }

    public void deleteTicket(int ticketId) throws Exception {
        Ticket ticket = ticketDAO.getById(ticketId);

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        int customerId = ticket.getCustomerId();
        ticketDAO.delete(ticketId);
        int remaining = ticketDAO.getTotalTicketsByCustomer(customerId, ticket.getEventId());

        if (remaining == 0) {
            customerDAO.delete(customerId);
        }
    }

    public List<IssuedTicket> getIssuedTickets(int eventId) {
        try {
            return ticketDAO.getIssuedTicketsByEvent(eventId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String generateBarcodeValue(int customerId, int eventId) {
        return "T-" + eventId + "-" + UUID.randomUUID();
    }

    public int getAvailableTickets(int ticketTypeId) throws Exception {
        TicketType tt = ticketTypeDAO.getById(ticketTypeId);

        int sold = ticketDAO.getTotalSoldForTicketType(ticketTypeId);

        return tt.getMaxQuantity() - sold;
    }
    public int getTotalTicketsSoldForEvent(int eventId) throws Exception {

        int total = 0;

        // 🔥 use YOUR existing method name
        List<TicketType> ticketTypes = ticketTypeDAO.getTicketTypesForEvent(eventId);

        for (TicketType type : ticketTypes) {
            int sold = ticketTypeDAO.getTicketCountForTicketType(type.getId());
            total += sold;
        }

        return total;
    }

    public int getTotalMaxTicketsForEvent(int eventId) throws Exception {
// 1. Fetch the list of ALL events from the DAO (or cache)
        List<Event> allEvents = eventDAO.getAllEvents();

        // 2. Look for the event with the matching ID
        for (Event e : allEvents) {
            if (e.getId() == eventId) {
                return e.getMaxTickets(); // Found it!
            }
        }

        return 0; // Not found
    }}