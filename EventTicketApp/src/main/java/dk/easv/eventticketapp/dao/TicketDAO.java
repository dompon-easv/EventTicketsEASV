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

    public List<IssuedTicket> getIssuedTicketsByEvent(int eventId) throws Exception {
        List<IssuedTicket> tickets = new ArrayList<>();

        String sql = """
        SELECT 
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
}