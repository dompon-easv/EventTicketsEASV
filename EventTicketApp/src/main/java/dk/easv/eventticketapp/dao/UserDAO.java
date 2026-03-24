package dk.easv.eventticketapp.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class UserDAO implements IUserDAO {

    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM dbo.Users WHERE username = ? AND password = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("connected" + conn.getCatalog());

            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("match");
                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    String userRole = rs.getString("role");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String userPassword = rs.getString("password");
                    String userName = rs.getString("username");

                    System.out.println("DB username = [" + userName + "]");
                    System.out.println("DB password = [" + userPassword + "]");
                    System.out.println("DB role = [" + userRole + "]");

                    UserRole role = UserRole.valueOf(userRole.toUpperCase());

                    return new User(email, role, name, surname, password, userName);
                } else {
                    System.out.println("no match");
                }
            }
            return null;
        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (email, role, name, surname, password, username) VALUES (?, ?,?,?,?,?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("connected" + conn.getCatalog());

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getRole().name().toLowerCase());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getName());
            stmt.setString(5, user.getSurname());
            stmt.setString(6, user.getUsername());
            stmt.executeUpdate();

        }
    }

    @Override
    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM dbo.Users WHERE username = ?";
        try (Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("connected" + conn.getCatalog());

            stmt.setString(1, username.trim());
            stmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Users";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            System.out.println("connected" + conn.getCatalog());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                    rs.getString("email"),
                    UserRole.valueOf(rs.getString("role").toUpperCase()),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("password"),
                    rs.getString("username") );
                    users.add(user);
                }
            }
        } return  users;
    }
}


