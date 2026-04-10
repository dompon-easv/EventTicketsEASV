package dk.easv.eventticketapp;

import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.dao.*;
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
        ITicketDAO ticketDAO = new TicketDAO();
        ICustomerDAO customerDAO = new CustomerDAO();

        AuthenticationLogic authenticationLogic = new AuthenticationLogic(userDAO);
        UserManager userManager = new UserManager(userDAO);
        EventLogic eventLogic = new EventLogic();
        EventCoordinatorLogic eventCoordinatorLogic = new EventCoordinatorLogic();
        TicketTypeManager ticketTypeManager = new TicketTypeManager(ticketTypeDAO);
        TicketManager ticketManager = new TicketManager(ticketDAO, ticketTypeDAO, customerDAO);
        CustomerLogic customerLogic = new CustomerLogic();

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setAuthenticationLogic(authenticationLogic);
        loginController.setUserManager(userManager);
        loginController.setEventLogic(eventLogic);
        loginController.setEventCoordinatorLogic(eventCoordinatorLogic);
        loginController.setTicketTypeManager(ticketTypeManager);
        loginController.setTicketManager(ticketManager);
        loginController.setCustomerLogic(customerLogic);

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
