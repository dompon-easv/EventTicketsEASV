package dk.easv.eventticketapp.gui.coordinatorControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class CoordinatorMainController {

    @FXML private StackPane contentArea;

    public void showHome(ActionEvent actionEvent) {
        loadView("CoordinatorHome.fxml");

    }
    private void loadView(String fxml) {
        try {
            // Logic to clear the old view and add the new one
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/" + fxml)));
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
