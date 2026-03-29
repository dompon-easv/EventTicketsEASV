package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.User;

import java.util.List;

public interface IEventCoordinatorDAO {
    void addCoordinatorToEvent(int eventId, int userId) throws Exception;

    // 🔥 NEW
    List<Integer> getUserIdsByEventId(int eventId) throws Exception;

    // 🔥 NEW
    void deleteCoordinatorsByEventId(int eventId) throws Exception;
    // 🔥 NEW biatch
    List<Integer> getEventIdsByUser(int userId) throws Exception;

    void deleteEvent(int id) throws Exception;

    List<User> getUsersByEventId(int eventId) throws Exception;
}