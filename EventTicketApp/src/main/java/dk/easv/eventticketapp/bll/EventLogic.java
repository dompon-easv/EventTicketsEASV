package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.dao.EventDAO;
import dk.easv.eventticketapp.dao.IEventDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventLogic {

    private final IEventDAO eventDAO;

    public EventLogic() {
        eventDAO = new EventDAO();
    }

    public Event createEvent(Event event) throws Exception {

        // Basic validation
        if (event.getName().isEmpty())
            throw new Exception("Event name is required");

        if (event.getLocation().isEmpty())
            throw new Exception("Location is required");

        if (event.getStartDate() == null)
            throw new Exception("Start date is required");

        return eventDAO.createEvent(event);
    }

    public int getEventCount() {
        try{
           return eventDAO.getEventCount();
        } catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Event> getAllEvents() {
        try {
            return eventDAO.getAllEvents();
        } catch (Exception e) {
            throw new RuntimeException("Error while getting all events", e);
        }
    }
}
