package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Ticket;
import java.sql.*;

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
}