package dk.easv.eventticketapp.dao;

import java.util.List;

public interface IEventCoordinatorDAO {
    void addCoordinatorToEvent(int eventId, int userId) throws Exception;

    // 🔥 NEW
    List<Integer> getUserIdsByEventId(int eventId) throws Exception;

    // 🔥 NEW
    void deleteCoordinatorsByEventId(int eventId) throws Exception;
}