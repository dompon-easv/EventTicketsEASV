package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class EventHeaderController {

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label coordinatorLabel;

    @FXML
    public StackPane contentArea;
    @FXML public Button btnOverview;
    @FXML public Button btnTicketTypes;
    @FXML public Button btnIssueTickets;
    @FXML public Button btnIssuedTickets;

    private Event currentEvent;
    private TicketTypeManager ticketTypeManager;

    public void setTicketTypeManager(TicketTypeManager manager) {
        this.ticketTypeManager = manager;
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        if (ticketTypeManager != null) {
            ticketTypeManager.setCurrentEvent(event);
        }

        titleLabel.setText(event.getName());

        String formattedDate = event.getStartDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm"));

        dateLabel.setText("📅 " + formattedDate);
        locationLabel.setText("📍 " + event.getLocation());

        try {
            EventCoordinatorLogic logic = new EventCoordinatorLogic();
            int count = logic.getCoordinatorIdsForEvent(event.getId()).size();
            coordinatorLabel.setText("👥 " + count + " coordinators");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load default tab
        loadView("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/CoordinatorEventOverview.fxml");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/CoordinatorHome.fxml"
                    )
            );

            Node node = loader.load();
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditEvent.fxml"
                    )
            );

            Node node = loader.load();

            var controller = loader.getController();
            controller.getClass()
                    .getMethod("populateEvent", Event.class)
                    .invoke(controller, currentEvent);

            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTabChange(ActionEvent event) {
        Button clicked = (Button) event.getSource();

        resetTabStyles();
        clicked.getStyleClass().add("active");

        switch (clicked.getId()) {
            case "btnOverview" ->
                    loadView("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/CoordinatorEventOverview.fxml");

            case "btnTicketTypes" -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/TicketTypes.fxml"
                    ));
                    Node view = loader.load();

                    // Pass the current event to the TicketTypesController
                    TicketTypesController controller = loader.getController();
                    controller.setEvent(currentEvent);
                    controller.setTicketTypeManager(ticketTypeManager);

                    contentArea.getChildren().setAll(view);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            case "btnIssueTickets" ->
                    loadView("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/IssueTickets.fxml");

            case "btnIssuedTickets" ->
                    loadView("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/IssuedTickets.fxml");
        }
    }

    private void loadView(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetTabStyles() {
        btnOverview.getStyleClass().remove("active");
        btnTicketTypes.getStyleClass().remove("active");
        btnIssueTickets.getStyleClass().remove("active");
        btnIssuedTickets.getStyleClass().remove("active");
    }
}