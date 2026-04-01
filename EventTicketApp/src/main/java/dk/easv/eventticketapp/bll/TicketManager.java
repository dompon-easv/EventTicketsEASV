package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Ticket;
import dk.easv.eventticketapp.dao.ITicketDAO;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {

    private final ITicketDAO ticketDAO;

    public TicketManager(ITicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public void issueTicket(int quantity, int eventId, int ticketTypeId, int consumerId) throws Exception {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
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