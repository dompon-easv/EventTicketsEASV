package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Customer;

import java.sql.*;

public class CustomerDAO implements ICustomerDAO {

    @Override
    public Customer createCustomer(Customer customer) throws Exception {
        String sql = "INSERT INTO Customers (name, email) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                customer.setId(rs.getInt(1));
            }

            return customer;
        }
    }

    @Override
    public Customer getByEmail(String email) throws Exception {
        String sql = "SELECT * FROM Customers WHERE email = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            }
            return null;
        }
    }

    @Override
    public void delete(int customerId) throws Exception {
        String sql = "DELETE FROM Customers WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }
}