package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Ticket;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.dao.CustomerDAO;
import dk.easv.eventticketapp.dao.ICustomerDAO;
import dk.easv.eventticketapp.dao.ITicketDAO;
import dk.easv.eventticketapp.dao.ITicketTypeDAO;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {

    private final ITicketDAO ticketDAO;
    private final ITicketTypeDAO ticketTypeDAO;
    private final ICustomerDAO customerDAO;

    private static final int MAX_TICKETS_PER_CUSTOMER = 5;

    public TicketManager(ITicketDAO ticketDAO, ITicketTypeDAO ticketTypeDAO, ICustomerDAO customerDAO) {
        this.ticketDAO = ticketDAO;
        this.ticketTypeDAO = ticketTypeDAO;
        this.customerDAO = customerDAO;
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

        Ticket ticket = new Ticket(quantity, eventId, ticketTypeId, customerId);
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
}