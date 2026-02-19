package dk.easv.eventticketapp.gui.coordinatorControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;
import java.util.Objects;

public class AddEventController {

    public void closeBtn(ActionEvent actionEvent) {
        try {
            // Load the CoordinatorHome.fxml view
            Node node = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/CoordinatorHome.fxml"
                    ))
            );

            // Inject it into the main content area
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
