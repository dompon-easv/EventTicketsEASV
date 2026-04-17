package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.enums.UserRole;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.adminControllers.AdminMainController;
import dk.easv.eventticketapp.gui.adminControllers.EventsController;
import dk.easv.eventticketapp.gui.coordinatorControllers.AddEditEventController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class EventHeaderController implements ApplicationServicesAware {

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label coordinatorLabel;

    @FXML public StackPane contentArea;
    @FXML public Button btnOverview;
    @FXML public Button btnTicketTypes;
    @FXML public Button btnIssueTickets;
    @FXML public Button btnIssuedTickets;

    private Event currentEvent;

    private CoordinatorMainController coordinatorMainController;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }
    public void setCoordinatorMainController(CoordinatorMainController coordinatorMainController) {
        this.coordinatorMainController = coordinatorMainController;
    }


    public void setEvent(Event event) {
        this.currentEvent = event;
        if (services.getTicketTypeManager() != null) {
            services.getTicketTypeManager().setCurrentEvent(event);
        }

        titleLabel.setText(event.getName());

        dateLabel.setText("📅 " + EventDateTimeFormatter.formatEventRange(event));
        locationLabel.setText("📍 " + event.getLocation());

        try {
            EventCoordinatorLogic logic = new EventCoordinatorLogic();
            int count = logic.getCoordinatorIdsForEvent(event.getId()).size();
            coordinatorLabel.setText("👥 " + count + " coordinators");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load default tab
        try {
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/CoordinatorEventOverview.fxml"
            );

            Node view = loader.load();

            CoordinatorEventOverviewController controller = loader.getController();
            controller.setEvent(currentEvent);

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if (SessionManager.getCurrentUser().getRole() == UserRole.ADMIN) {
            btnOverview.setVisible(false);
            btnTicketTypes.setVisible(false);
            btnIssueTickets.setVisible(false);
            btnIssuedTickets.setVisible(false);
        }

    }

    @FXML
    private void handleBack() {
        if (SessionManager.getCurrentUser().getRole() == UserRole.ADMIN) {
            try {
                FXMLLoader loader = new ViewFactory(services).createLoader("gui/adminViews/Events.fxml"
                );

                Node node = loader.load();
                AdminMainController.staticContentArea.getChildren().setAll(node);
                Object controller = loader.getController();
                if (controller instanceof EventsController eventsController) {
                    eventsController.init();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            coordinatorMainController.loadView("CoordinatorHome.fxml");
        }
    }

    @FXML
    private void handleEditEvent() {
        try {
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/AddEditEvent.fxml"

            );

            Node node = loader.load();
            AddEditEventController controller = loader.getController();
            controller.populateEvent(currentEvent);
            controller.setCoordinatorMainController(coordinatorMainController);
            controller.init();

            if (SessionManager.getCurrentUser().getRole() == UserRole.ADMIN) {
                AdminMainController.staticContentArea.getChildren().setAll(node);
            } else {
            CoordinatorMainController.staticContentArea.getChildren().setAll(node); }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTabChange(ActionEvent actionEvent) {
        Button clicked = (Button) actionEvent.getSource();

        resetTabStyles();
        clicked.getStyleClass().add("active");

        switch (clicked.getId()) {
            case "btnOverview" -> {
                try {
                    FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/CoordinatorEventOverview.fxml"
                    );

                    Node view = loader.load();

                    CoordinatorEventOverviewController controller = loader.getController();
                    controller.setEvent(currentEvent);

                    contentArea.getChildren().setAll(view);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            case "btnTicketTypes" -> {
                try {
                    FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/TicketTypes.fxml"
                    );
                    Node view = loader.load();

                    TicketTypesController controller = loader.getController();
                    controller.setEvent(currentEvent);
                    controller.setContentArea(contentArea);

                    contentArea.getChildren().setAll(view);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            case "btnIssueTickets" -> {
                try {
                    FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/IssueTickets.fxml"
                    );
                    Node view = loader.load();

                    IssueTicketsController controller = loader.getController();
                    controller.setEvent(currentEvent);

                    contentArea.getChildren().setAll(view);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            case "btnIssuedTickets" -> {
                try {
                    FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/IssuedTickets.fxml"
                    );

                    Node view = loader.load();

                    IssuedTicketsController controller = loader.getController();
                    controller.loadTickets(currentEvent.getId());
                    controller.setContentArea(contentArea);
                    controller.setEvent(currentEvent);

                    contentArea.getChildren().setAll(view);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void resetTabStyles() {
        btnOverview.getStyleClass().remove("active");
        btnTicketTypes.getStyleClass().remove("active");
        btnIssueTickets.getStyleClass().remove("active");
        btnIssuedTickets.getStyleClass().remove("active");
    }
}