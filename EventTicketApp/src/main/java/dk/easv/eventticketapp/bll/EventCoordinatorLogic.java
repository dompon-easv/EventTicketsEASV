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
}