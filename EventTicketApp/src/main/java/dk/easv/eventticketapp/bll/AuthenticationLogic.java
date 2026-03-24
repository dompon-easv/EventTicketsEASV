package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.dao.IUserDAO;

public class AuthenticationLogic {
    private final IUserDAO userDAO;

    public AuthenticationLogic(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.getUserByUsernameAndPassword(username, password);
    }
}
