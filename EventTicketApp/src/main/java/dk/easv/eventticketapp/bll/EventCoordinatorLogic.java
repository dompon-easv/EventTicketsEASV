package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.dao.EventCoordinatorDAO;
import dk.easv.eventticketapp.dao.IEventCoordinatorDAO;

import java.util.List;

public class EventCoordinatorLogic {

    private final IEventCoordinatorDAO dao = new EventCoordinatorDAO();

    public void assignCoordinators(int eventId, List<Integer> userIds) throws Exception {
        for (int userId : userIds) {
            dao.addCoordinatorToEvent(eventId, userId);
        }
    }

    // 🔥 NEW
    public List<Integer> getCoordinatorIdsForEvent(int eventId) throws Exception {
        return dao.getUserIdsByEventId(eventId);
    }

    // 🔥 NEW (replace all coordinators)
    public void updateCoordinators(int eventId, List<Integer> userIds) throws Exception {
        dao.deleteCoordinatorsByEventId(eventId); // remove old

        for (int userId : userIds) {
            dao.addCoordinatorToEvent(eventId, userId); // insert new
        }
    }
}