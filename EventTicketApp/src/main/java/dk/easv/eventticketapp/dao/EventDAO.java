package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.UserRole;

import java.sql.*;

public class EventDAO implements IEventDAO {

    private final ConnectionManager connectionManager;

    public EventDAO() {
        connectionManager = new ConnectionManager();
    }

    @Override
    public Event createEvent(Event event) throws Exception {

        String sql = """
            INSERT INTO Events (name, location, startDate, endDate, description, location_description)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getLocation());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));

            if (event.getEndDate() != null)
                stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            else
                stmt.setNull(4, Types.TIMESTAMP);

            stmt.setString(5, event.getDescription());
            stmt.setString(6, event.getLocationDescription());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Event(
                        id,
                        event.getName(),
                        event.getLocation(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription(),
                        event.getLocationDescription()
                );
            }
        }

        return null;
    }

    @Override
    public int getEventCount() throws SQLException {
        int eventCount;
        String sql = "SELECT COUNT(*) FROM Events";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return eventCount = rs.getInt(1);
                }
            }

        } return 0;
    }
}