package dk.easv.eventticketapp;

import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.dao.IUserDAO;
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
        AuthenticationLogic authenticationLogic = new AuthenticationLogic(userDAO);
        UserManager userManager = new UserManager(userDAO);
        EventLogic eventLogic = new EventLogic();

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setAuthenticationLogic(authenticationLogic);
        loginController.setUserManager(userManager);
        loginController.setEventLogic(eventLogic);


        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
