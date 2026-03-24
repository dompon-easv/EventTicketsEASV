package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.User;

public interface IUserDAO {

    User getUserByUsernameAndPassword(String username, String password);
}
