package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.io.IOException;

public class TicketTypesController {
    public Button btnAddTicketType;

    public void onAddTicketType(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditTicketTypes.fxml")
            );

            Node node = loader.load();
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEditTicketType(ActionEvent actionEvent) {
    }

    public void onDeleteTicketType(ActionEvent actionEvent) {
    }
}
