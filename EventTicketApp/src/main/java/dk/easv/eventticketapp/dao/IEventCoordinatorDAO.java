package dk.easv.eventticketapp.dao;

public interface IEventCoordinatorDAO {
    void addCoordinatorToEvent(int eventId, int userId) throws Exception;
}