package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.LoginController;
import dk.easv.eventticketapp.gui.adminControllers.EventsController;
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

public class CoordinatorMainController implements ApplicationServicesAware {

    @FXML
    Label lblUser;
    @FXML
    Label lblRole;
    @FXML
    Label lblInitials;
    @FXML
    StackPane contentArea;

    public static StackPane staticContentArea;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    public void initialize() {
        staticContentArea = contentArea;

        lblUser.setText(SessionManager.getCurrentUser().getName() + " " + SessionManager.getCurrentUser().getSurname());
        lblRole.setText(SessionManager.getCurrentUser().getRole().toString());
        lblInitials.setText(String.valueOf(SessionManager.getCurrentUser().getName().charAt(0)) + " " + String.valueOf(SessionManager.getCurrentUser().getSurname().charAt(0)));
    }

    public void init() {
        loadView("CoordinatorHome.fxml");
    }

    public void showHome(ActionEvent actionEvent) {
        loadView("CoordinatorHome.fxml");
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadView(String fxml) {
        try {
            ViewFactory viewFactory = new ViewFactory(services);
            FXMLLoader loader = viewFactory.createLoader("gui/coordinatorViews/" + fxml);

            Node node = loader.load();
            Object controller = loader.getController();

            if (controller instanceof CoordinatorHomeController coordinatorHomeController) {
                coordinatorHomeController.setMainCoordinatorController(this);
                coordinatorHomeController.init();
            }

            if (controller instanceof VouchersOverviewController vouchersOverviewController) {
                vouchersOverviewController.setMainCoordinatorController(this);
            }

            contentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

