package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminMainController {

    @FXML
    private StackPane contentArea;

    // Optional: automatically load default view
    public void initialize() {
        loadView("AdminHome.fxml");
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxml) {
        try {
            Node node = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/dk/easv/eventticketapp/gui/adminViews/" + fxml
                            )
                    )
            );
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
