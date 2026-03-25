package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.bll.AuthenticationLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.SessionManager;
import dk.easv.eventticketapp.bll.UserManager;
import dk.easv.eventticketapp.gui.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CoordinatorMainController {

    @FXML Label lblUser;
    @FXML Label lblRole;
    @FXML Label lblInitials;
    @FXML StackPane contentArea;

    public static StackPane staticContentArea;

    private AuthenticationLogic authenticationLogic;
    private SessionManager sessionManager;
    private UserManager userManager;
    private EventLogic eventLogic;

    public void initialize() {
        staticContentArea = contentArea;
        loadView("CoordinatorHome.fxml");
        lblUser.setText(SessionManager.getCurrentUser().getName() + " " + SessionManager.getCurrentUser().getSurname());
        lblRole.setText(SessionManager.getCurrentUser().getRole().toString());
        lblInitials.setText(String.valueOf(SessionManager.getCurrentUser().getName().charAt(0)) + String.valueOf(SessionManager.getCurrentUser().getSurname().charAt(0)));
    }

    public void showHome(ActionEvent actionEvent) {
        loadView("CoordinatorHome.fxml");
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
            Node node = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/dk/easv/eventticketapp/gui/coordinatorViews/" + fxml
                            )
                    )
            );
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthenticationLogic(AuthenticationLogic authenticationLogic) {
        this.authenticationLogic = authenticationLogic;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }
}
