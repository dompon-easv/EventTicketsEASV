package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;

import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {

    User getUserByUsernameAndPassword(String username, String password);

    void addUser(User user) throws SQLException;

    List<User> getAllUsers() throws SQLException;

    void deleteUser(String username) throws SQLException;
}
