package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;

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
    @Override
    public List<Integer> getEventIdsByUser(int userId) throws Exception {
        List<Integer> eventIds = new ArrayList<>();

        String sql = "SELECT eventId FROM EventCoordinators WHERE userId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventIds.add(rs.getInt("eventId"));
            }
        }

        return eventIds;
    }

    @Override
    public void deleteEvent(int id) throws SQLException {
        String sql = "DELETE FROM EventCoordinators WHERE eventId = ?";
        try (Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<User> getUsersByEventId(int eventId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM Users u  JOIN EventCoordinators ec ON u.id = ec.userId WHERE ec.eventId = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            UserRole.valueOf(rs.getString("role").toUpperCase()),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("password"),
                            rs.getString("username")
                    );

                    users.add(user);
                }
            }
        }

        return users;

    }
}

