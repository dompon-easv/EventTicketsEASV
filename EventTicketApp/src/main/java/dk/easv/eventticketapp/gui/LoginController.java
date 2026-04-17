package dk.easv.eventticketapp.gui;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.enums.UserRole;
import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.bll.SessionManager;
import dk.easv.eventticketapp.gui.adminControllers.AdminMainController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController implements ApplicationServicesAware {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    @FXML
    private void onLoginAction(ActionEvent actionEvent) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            User user = services.getAuthenticationLogic().login(username, password);

            if (user == null) {
                System.out.println("Wrong username or password");
                return;
            }

            SessionManager.setCurrentUser(user);
            loadMainView(actionEvent, user.getRole());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMainView(ActionEvent actionEvent, UserRole role) throws IOException {
        String fxmlPath = switch (role) {
            case ADMIN -> "gui/adminViews/AdminMain.fxml";
            case COORDINATOR -> "gui/coordinatorViews/CoordinatorMain.fxml";
        };

        ViewFactory viewFactory = new ViewFactory(services);
        FXMLLoader loader = viewFactory.createLoader(fxmlPath);
        Scene scene = new Scene(loader.load());

        Object controller = loader.getController();
        if (controller instanceof AdminMainController adminMainController) {
            adminMainController.init();
        }
        if (controller instanceof CoordinatorMainController coordinatorMainController) {
            coordinatorMainController.init();
        }

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}