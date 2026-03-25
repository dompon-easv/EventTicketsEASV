package dk.easv.eventticketapp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // 🔥 GET coordinators
    @Override
    public List<Integer> getUserIdsByEventId(int eventId) throws Exception {
        List<Integer> userIds = new ArrayList<>();

        String sql = "SELECT userId FROM EventCoordinators WHERE eventId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("userId"));
                }
            }
        }

        return userIds;
    }

    // 🔥 DELETE coordinators (for update)
    @Override
    public void deleteCoordinatorsByEventId(int eventId) throws Exception {
        String sql = "DELETE FROM EventCoordinators WHERE eventId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }
}

