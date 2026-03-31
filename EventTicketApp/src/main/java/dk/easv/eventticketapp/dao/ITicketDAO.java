package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Ticket;

public interface ITicketDAO {
    void add(Ticket ticket) throws Exception;
}