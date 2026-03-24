package dk.easv.eventticketapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EventCoordinatorDAO implements IEventCoordinatorDAO {

    private final ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void addCoordinatorToEvent(int eventId, int userId) throws Exception {

        String sql = "INSERT INTO EventCoordinators (eventId, userId) VALUES (?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}

