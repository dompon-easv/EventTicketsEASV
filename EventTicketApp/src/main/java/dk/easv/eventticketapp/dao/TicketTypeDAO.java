package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.TicketType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketTypeDAO implements ITicketTypeDAO {

    public TicketTypeDAO() {
    }

    @Override
    public void add(TicketType ticketType) throws Exception {
        String sql = "INSERT INTO TicketTypes (name, description, price, eventId, quantityAvailable) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ticketType.getName());
            stmt.setString(2, ticketType.getDescription());
            stmt.setDouble(3, ticketType.getPrice());
            stmt.setInt(4, ticketType.getEventId());
            stmt.setInt(5, ticketType.getQuantityAvailable());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticketType.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(TicketType ticketType) throws Exception {
        String sql = "UPDATE TicketTypes SET name=?, description=?, price=?, eventId=?, quantityAvailable=? WHERE id=?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticketType.getName());
            stmt.setString(2, ticketType.getDescription());
            stmt.setDouble(3, ticketType.getPrice());
            stmt.setInt(4, ticketType.getEventId());
            stmt.setInt(5, ticketType.getQuantityAvailable());
            stmt.setInt(6, ticketType.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM TicketTypes WHERE id=?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<TicketType> getTicketTypesForEvent(int eventId) throws Exception {
        String sql = "SELECT * FROM TicketTypes WHERE eventId=?";
        List<TicketType> ticketTypes = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ticketTypes.add(new TicketType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("eventId"),
                            rs.getInt("quantityAvailable")
                    ));
                }
            }
        }
        return ticketTypes;
    }

    @Override
    public TicketType getById(int id) throws Exception {
        String sql = "SELECT * FROM TicketTypes WHERE id=?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TicketType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("eventId"),
                            rs.getInt("quantityAvailable")
                    );
                }
            }
        }
        return null;
    }
}