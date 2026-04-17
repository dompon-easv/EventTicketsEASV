package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.bll.*;
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

public class AdminMainController implements ApplicationServicesAware {

    @FXML Label lblUser;
    @FXML Label lblRole;
    @FXML Label lblInitials;
    @FXML StackPane contentArea;
    @FXML
    public static StackPane staticContentArea;


    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    public void init()
    {
        loadView("UserManagement.fxml");
    }
    public void initialize() {

        staticContentArea = contentArea;
        lblUser.setText(SessionManager.getCurrentUser().getName() + " " +SessionManager.getCurrentUser().getSurname());
        lblRole.setText(SessionManager.getCurrentUser().getRole().toString());
        lblInitials.setText(String.valueOf(SessionManager.getCurrentUser().getName().charAt(0)) + " " + String.valueOf(SessionManager.getCurrentUser().getSurname().charAt(0)));
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
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/Login.fxml");

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/adminViews/" + fxml);

            Node node = loader.load();
            Object controller = loader.getController();

            if (controller instanceof UserManagementController userManagementController) {
                userManagementController.loadUsers();
            }

            if (controller instanceof EventsController eventsController) {
                eventsController.init();
            }

            contentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
