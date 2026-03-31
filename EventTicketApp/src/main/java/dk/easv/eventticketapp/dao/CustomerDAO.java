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
}