package dk.easv.eventticketapp.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {

    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM dbo.Users WHERE username = ? AND password = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("connected " + conn.getCatalog());

            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    String roleStr = rs.getString("role");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String userPassword = rs.getString("password");
                    String userName = rs.getString("username");

                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());

                    // ✅ FIX: return WITH ID
                    return new User(id, email, role, name, surname, userPassword, userName);
                }
            }

        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (email, role, name, surname, password, username) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("connected " + conn.getCatalog());

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getRole().name().toLowerCase());

            // ✅ FIXED ORDER
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getSurname());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getUsername());

            stmt.executeUpdate();
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Users";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("connected " + conn.getCatalog());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    User user = new User(
                            rs.getInt("id"), // ✅ CRITICAL FIX
                            rs.getString("email"),
                            UserRole.valueOf(rs.getString("role").toUpperCase()),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("password"),
                            rs.getString("username")
                    );

                    users.add(user); // ✅ CRITICAL FIX
                }
            }
        }

        return users;
    }

    @Override
    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM Users WHERE username = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    @Override
    public void editUser(User updatedUser) throws SQLException {
        String sql = "UPDATE Users SET email = ?, role = ?, name = ?, surname = ?, password = ?, username = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, updatedUser.getEmail());
            stmt.setString(2, updatedUser.getRole().name().toLowerCase());
            stmt.setString(3, updatedUser.getName());
            stmt.setString(4, updatedUser.getSurname());
            stmt.setString(5, updatedUser.getPassword());
            stmt.setString(6, updatedUser.getUsername());
            stmt.setInt(7, updatedUser.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public int getCoordinatorCount() throws SQLException
    {
        int coordinatorCount;
        String sql1 = "SELECT COUNT(*) FROM dbo.Users WHERE role = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql1)) {
            stmt.setString(1, UserRole.COORDINATOR.name().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return coordinatorCount = rs.getInt(1);
                }
            }

    } return 0;


} }