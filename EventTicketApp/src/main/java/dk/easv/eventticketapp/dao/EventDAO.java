package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    @Override
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Events";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp startDate = rs.getTimestamp("startDate");
                    Timestamp endDate = rs.getTimestamp("endDate");

                    Event event = new Event(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("location"),
                            startDate != null ? startDate.toLocalDateTime() : null,
                            endDate != null ? endDate.toLocalDateTime() : null,
                            rs.getString("description"),
                            rs.getString("location_description")
                    );

                    events.add(event);
                }
            }


        return events;
    }
    @Override
    public void updateEvent(Event event) throws Exception {

        String sql = """
        UPDATE Events
        SET name = ?, location = ?, startDate = ?, endDate = ?, description = ?, location_description = ?
        WHERE id = ?
    """;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getLocation());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));

            if (event.getEndDate() != null)
                stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            else
                stmt.setNull(4, Types.TIMESTAMP);

            stmt.setString(5, event.getDescription());
            stmt.setString(6, event.getLocationDescription());

            stmt.setInt(7, event.getId());

            stmt.executeUpdate();
        }
    }
    @Override
    public List<Event> getEventsByIds(List<Integer> ids) throws Exception {
        List<Event> events = new ArrayList<>();

        if (ids == null || ids.isEmpty()) return events;

        String placeholders = String.join(",", ids.stream().map(id -> "?").toList());
        String sql = "SELECT * FROM Events WHERE id IN (" + placeholders + ")";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getTimestamp("startDate") != null ? rs.getTimestamp("startDate").toLocalDateTime() : null,
                        rs.getTimestamp("endDate") != null ? rs.getTimestamp("endDate").toLocalDateTime() : null,
                        rs.getString("description"),
                        rs.getString("location_description")
                ));
            }
        }

        return events;
    }
}