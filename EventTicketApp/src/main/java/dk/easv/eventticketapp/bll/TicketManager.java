package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Ticket;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.dao.ITicketDAO;
import dk.easv.eventticketapp.dao.ITicketTypeDAO;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {

    private final ITicketDAO ticketDAO;
    private final ITicketTypeDAO ticketTypeDAO;

    public TicketManager(ITicketDAO ticketDAO, ITicketTypeDAO ticketTypeDAO) {
        this.ticketDAO = ticketDAO;
        this.ticketTypeDAO = ticketTypeDAO;
    }

    public void issueTicket(int quantity, int eventId, int ticketTypeId, int consumerId) throws Exception {
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

        Ticket ticket = new Ticket(quantity, eventId, ticketTypeId, consumerId);
        ticketDAO.add(ticket);
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