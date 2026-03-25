package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;
import dk.easv.eventticketapp.dao.IUserDAO;

import java.sql.SQLException;
import java.util.List;

public class UserManager {
    private IUserDAO userDAO;

    public UserManager(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void addUser(String email, UserRole role, String name, String surname, String password, String username) {
        try {
            userDAO.addUser(new User(email, role, name, surname, password, username));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(String username) {
        try {
            userDAO.deleteUser(username);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editUser(User updatedUser) {
        try {
            userDAO.editUser(updatedUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCoordinatorCount()
    {
        try {
            return userDAO.getCoordinatorCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
