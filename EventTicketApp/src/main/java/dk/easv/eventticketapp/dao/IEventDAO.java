package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Event;

public interface IEventDAO {
    Event createEvent(Event event) throws Exception;
    int getEventCount() throws Exception;
}