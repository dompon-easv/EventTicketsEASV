package dk.easv.eventticketapp.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO implements IUserDAO {

    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM dbo.Users WHERE username = ? AND password = ?";

        System.out.println("DAO username = [" + username + "]");
        System.out.println("DAO password = [" + password + "]");
        System.out.println("SQL = " + sql);

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("connected" + conn.getCatalog());

            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            try(ResultSet rs = stmt.executeQuery())
            {
                if(rs.next()) {
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

                    return new User(id, email, role, name, surname, password, userName);
                } else  {
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
    }


