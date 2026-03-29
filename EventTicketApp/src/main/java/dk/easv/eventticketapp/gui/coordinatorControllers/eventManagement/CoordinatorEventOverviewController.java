package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.gui.adminControllers.UserCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class CoordinatorEventOverviewController {

    @FXML private Label lblDescription;
    @FXML private VBox coordinatorContainer;

    private Event currentEvent;

    public void setEvent(Event event) {
        this.currentEvent = event;

        if (event == null) {
            System.out.println("Event is NULL");
            return;
        }

        // ✅ Set description
        lblDescription.setText(event.getDescription());

        // ✅ Load coordinators
        loadCoordinators();
    }

    private void loadCoordinators() {
        try {
            if (currentEvent == null) {
                System.out.println("Current event is NULL");
                return;
            }

            EventCoordinatorLogic ecLogic = new EventCoordinatorLogic();

            List<User> coordinators =
                    ecLogic.getCoordinatorsForEvent(currentEvent.getId());

            System.out.println("Event ID: " + currentEvent.getId());
            System.out.println("Coordinators: " + coordinators);

            coordinatorContainer.getChildren().clear();

            if (coordinators == null || coordinators.isEmpty()) {
                System.out.println("No coordinators found");
                return;
            }

            for (User user : coordinators) {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/dk/easv/eventticketapp/gui/adminViews/UserCard.fxml"
                        )
                );

                VBox card = loader.load();

                UserCardController controller = loader.getController();
                controller.setUser(user);

                coordinatorContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}