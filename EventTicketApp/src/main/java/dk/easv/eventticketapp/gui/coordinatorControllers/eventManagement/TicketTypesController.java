package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import dk.easv.eventticketapp.gui.coordinatorControllers.AddEditTicketTypeController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.io.IOException;

public class TicketTypesController {

    public Button btnAddTicketType;
    private TicketTypeManager ticketTypeManager;
    private Event currentEvent;

    public void setTicketTypeManager(TicketTypeManager manager) {
        this.ticketTypeManager = manager;
        System.out.println("TicketTypeManager set in TicketTypesController: " + (manager != null ? "not null" : "null"));
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        System.out.println("Event set in TicketTypesController: " + (event != null ? event.getName() : "null"));

        // Set the current event in the manager
        if (ticketTypeManager != null && currentEvent != null) {
            ticketTypeManager.setCurrentEvent(currentEvent);
            System.out.println("Current event set in TicketTypeManager: " + currentEvent.getName());
        }
    }

    public void onAddTicketType(ActionEvent actionEvent) {
        try {
            // Verify we have the event before proceeding
            if (currentEvent == null) {
                System.err.println("ERROR: No event selected!");
                return;
            }

            if (ticketTypeManager == null) {
                System.err.println("ERROR: TicketTypeManager not initialized!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditTicketTypes.fxml")
            );

            Node node = loader.load();

            // Pass the current event and ticketTypeManager to the controller
            AddEditTicketTypeController controller = loader.getController();
            controller.setEvent(currentEvent);
            controller.setTicketTypeManager(ticketTypeManager);

            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEditTicketType(ActionEvent actionEvent) {
        // Implement edit functionality
    }

    public void onDeleteTicketType(ActionEvent actionEvent) {
        // Implement delete functionality
    }
}