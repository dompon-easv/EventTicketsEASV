package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO implements ITicketDAO {

    public void add(Ticket ticket) throws Exception {
        String sql = "INSERT INTO Tickets (quantity, eventId, ticketTypeId, customerId) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getQuantity());
            stmt.setInt(2, ticket.getEventId());
            stmt.setInt(3, ticket.getTicketTypeId());
            stmt.setInt(4, ticket.getCustomerId());
            stmt.executeUpdate();
        }
    }

    public void delete(int ticketId) throws Exception {
        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            stmt.executeUpdate();
        }
    }

    public List<IssuedTicket> getIssuedTicketsByEvent(int eventId) throws Exception {
        List<IssuedTicket> tickets = new ArrayList<>();

        String sql = """
        SELECT 
            t.id,
            c.name AS customerName,
            c.email,
            tt.name AS ticketType,
            t.quantity,
            (t.quantity * tt.price) AS totalPrice
        FROM Tickets t
        JOIN Customers c ON t.customerId = c.id
        JOIN TicketTypes tt ON t.ticketTypeId = tt.id
        WHERE t.eventId = ?
    """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(new IssuedTicket(
                        rs.getInt("id"),
                        rs.getString("customerName"),
                        rs.getString("email"),
                        rs.getString("ticketType"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalPrice")
                ));
            }
        }
        return tickets;
    }

    @Override
    public int getTotalSoldForTicketType(int ticketTypeId) throws Exception {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS totalSold FROM Tickets WHERE ticketTypeId = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketTypeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalSold");
            }
            return 0;
        }
    }

    @Override
    public Ticket getById(int ticketId) throws Exception {
        String sql = "SELECT * FROM Tickets WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Ticket(
                        rs.getInt("id"),
                        rs.getInt("quantity"),
                        rs.getInt("eventId"),
                        rs.getInt("ticketTypeId"),
                        rs.getInt("customerId")
                );
            }
            return null;
        }
    }

    @Override
    public int getTotalTicketsByCustomer(int customerId) throws Exception {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total FROM Tickets WHERE customerId = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }
}