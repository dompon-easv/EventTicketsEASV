package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.dao.EventCoordinatorDAO;
import dk.easv.eventticketapp.dao.EventDAO;
import dk.easv.eventticketapp.dao.IEventCoordinatorDAO;
import dk.easv.eventticketapp.dao.IEventDAO;

import java.util.ArrayList;
import java.util.List;

public class EventCoordinatorLogic {

    private final IEventCoordinatorDAO dao;
    private final IEventDAO eventDAO;

    public EventCoordinatorLogic() {
        this.dao = new EventCoordinatorDAO();
        this.eventDAO = new EventDAO();
    }

    // =============================
    // CREATE / UPDATE
    // =============================

    public void assignCoordinators(int eventId, List<Integer> userIds) throws Exception {
        for (int userId : userIds) {
            dao.addCoordinatorToEvent(eventId, userId);
        }
    }

    public void updateCoordinators(int eventId, List<Integer> userIds) throws Exception {
        dao.deleteCoordinatorsByEventId(eventId);

        for (int userId : userIds) {
            dao.addCoordinatorToEvent(eventId, userId);
        }
    }

    // =============================
    // READ (EVENT → USERS)
    // =============================

    public List<Integer> getCoordinatorIdsForEvent(int eventId) throws Exception {
        return dao.getUserIdsByEventId(eventId);
    }

    // =============================
    // READ (USER → EVENTS) ✅ NEW
    // =============================

    public List<Event> getEventsForUser(int userId) throws Exception {
        List<Integer> eventIds = dao.getEventIdsByUser(userId);

        if (eventIds == null || eventIds.isEmpty()) {
            return new ArrayList<>();
        }

        return eventDAO.getEventsByIds(eventIds);
    }

    // =============================
    // ADMIN FEATURES ✅ NEW
    // =============================

    // All event IDs for a user (raw access)
    public List<Integer> getEventIdsForUser(int userId) throws Exception {
        return dao.getEventIdsByUser(userId);
    }

    // Optional: full mapping (future-proof for admin dashboards)
    public List<Event> getAllEvents() throws Exception {
        return eventDAO.getAllEvents();
    }
}