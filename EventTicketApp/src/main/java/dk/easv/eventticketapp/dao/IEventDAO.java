package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Event;

import java.util.List;

public interface IEventDAO {
    Event createEvent(Event event) throws Exception;
    int getEventCount() throws Exception;

    List<Event> getAllEvents() throws Exception;
    void updateEvent(Event event) throws Exception;

    List<Event> getEventsByIds(List<Integer> ids) throws Exception;

    void deleteEvent(int id) throws Exception;
}
