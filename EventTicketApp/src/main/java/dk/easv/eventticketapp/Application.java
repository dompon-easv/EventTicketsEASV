package dk.easv.eventticketapp;

import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.dao.ITicketTypeDAO;
import dk.easv.eventticketapp.dao.IUserDAO;
import dk.easv.eventticketapp.dao.TicketTypeDAO;
import dk.easv.eventticketapp.dao.UserDAO;
import dk.easv.eventticketapp.gui.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        IUserDAO userDAO = new UserDAO();
        ITicketTypeDAO ticketTypeDAO = new TicketTypeDAO();

        AuthenticationLogic authenticationLogic = new AuthenticationLogic(userDAO);
        UserManager userManager = new UserManager(userDAO);
        EventLogic eventLogic = new EventLogic();
        EventCoordinatorLogic eventCoordinatorLogic = new EventCoordinatorLogic();
        TicketTypeManager ticketTypeManager = new TicketTypeManager(ticketTypeDAO);

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setAuthenticationLogic(authenticationLogic);
        loginController.setUserManager(userManager);
        loginController.setEventLogic(eventLogic);
        loginController.setEventCoordinatorLogic(eventCoordinatorLogic);
        loginController.setTicketTypeManager(ticketTypeManager);

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
