package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminMainController {

    @FXML Label lblUser;
    @FXML Label lblRole;
    @FXML Label lblInitials;
    @FXML StackPane contentArea;


    private SessionManager sessionManager;
    private AuthenticationLogic authenticationLogic;
    private UserManager userManager;
    private EventLogic eventLogic;
    private EventCoordinatorLogic eventCoordinatorLogic;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setAuthenticationLogic(AuthenticationLogic authenticationLogic) {
        this.authenticationLogic = authenticationLogic;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }
    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {this.eventCoordinatorLogic = eventCoordinatorLogic;}

    public void init()
    {
        loadView("UserManagement.fxml");
    }
    public void initialize() {


        lblUser.setText(SessionManager.getCurrentUser().getName() + " " +SessionManager.getCurrentUser().getSurname());
        lblRole.setText(SessionManager.getCurrentUser().getRole().toString());
        lblInitials.setText(String.valueOf(SessionManager.getCurrentUser().getName().charAt(0)) + String.valueOf(SessionManager.getCurrentUser().getSurname().charAt(0)));
    }

    public void showUsers(ActionEvent actionEvent) {
        loadView("UserManagement.fxml");
    }

    public void showEvents(ActionEvent actionEvent) {
        loadView("Events.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent actionEvent) {
        SessionManager.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(
                    Application.class.getResource("gui/Login.fxml")
            );

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/style.css")
                    ).toExternalForm()
            );

            Stage stage = (Stage) ((Node) actionEvent.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();

            LoginController loginController = loader.getController();
            loginController.setAuthenticationLogic(authenticationLogic);
            loginController.setUserManager(userManager);
            loginController.setEventLogic(eventLogic);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/dk/easv/eventticketapp/gui/adminViews/" + fxml
                            )
                    )
            );

            Node node = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserManagementController userManagementController) {
                userManagementController.setUserManager(userManager);
            }
            if (controller instanceof EventsController eventsController) {
                eventsController.setUserManager(userManager);
                eventsController.setEventLogic(eventLogic);
                eventsController.setEventCoordinatorLogic(eventCoordinatorLogic);
                eventsController.init();
            }
            contentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
