package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.User;

import java.util.List;

public interface IEventCoordinatorDAO {
    void addCoordinatorToEvent(int eventId, int userId) throws Exception;

    List<Integer> getUserIdsByEventId(int eventId) throws Exception;

    void deleteCoordinatorsByEventId(int eventId) throws Exception;

    List<Integer> getEventIdsByUser(int userId) throws Exception;

    void deleteEvent(int id) throws Exception;

    List<User> getUsersByEventId(int eventId) throws Exception;

    void deleteUser(int userId) throws Exception;
}