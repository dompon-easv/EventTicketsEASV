package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Ticket;

import java.util.List;

public interface ITicketDAO {
    void add(Ticket ticket) throws Exception;
    List<IssuedTicket> getIssuedTicketsByEvent(int eventId) throws Exception;
}