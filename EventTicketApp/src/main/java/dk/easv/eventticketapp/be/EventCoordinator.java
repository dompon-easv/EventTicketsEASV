package dk.easv.eventticketapp.be;

public class EventCoordinator {

    private int eventId;
    private int userId;

    public EventCoordinator(int eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public int getEventId() { return eventId; }
    public int getUserId() { return userId; }
}