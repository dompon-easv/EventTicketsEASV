package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.bll.AuthenticationLogic;
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

public class AdminMainController {

    public Label lblUser;
    public Label lblRole;
    @FXML
    private StackPane contentArea;

    private SessionManager sessionManager;
    private AuthenticationLogic authenticationLogic;
    private UserManager userManager;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setAuthenticationLogic(AuthenticationLogic authenticationLogic) {
        this.authenticationLogic = authenticationLogic;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // Optional: automatically load default view
    public void initialize() {
        loadView("AdminHome.fxml");
        lblUser.setText(SessionManager.getCurrentUser().getName() + " " +SessionManager.getCurrentUser().getSurname());
        lblRole.setText(SessionManager.getCurrentUser().getRole().toString());
    }

    public void showHome(ActionEvent actionEvent) {
        loadView("AdminHome.fxml");
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
            contentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
