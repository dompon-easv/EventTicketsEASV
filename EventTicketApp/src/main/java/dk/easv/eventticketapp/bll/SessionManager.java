package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser=user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clearSession()
    {
        currentUser=null;
    }

    public boolean isLoggedIn()
    {
        return currentUser!=null;
    }
}
