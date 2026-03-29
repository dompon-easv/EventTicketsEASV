package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.TicketType;
import java.util.List;

public interface ITicketTypeDAO {
    void add(TicketType ticketType) throws Exception;
    void update(TicketType ticketType) throws Exception;
    void delete(int id) throws Exception;
    List<TicketType> getTicketTypesForEvent(int eventId) throws Exception;
    TicketType getById(int id) throws Exception;
}