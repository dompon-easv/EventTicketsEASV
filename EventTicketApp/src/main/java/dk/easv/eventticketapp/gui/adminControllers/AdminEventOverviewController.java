package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.SessionManager;
import dk.easv.eventticketapp.bll.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AdminEventOverviewController {
    public ListView lstCoordinators;
    @FXML Label lblGuidance;
    @FXML
    Label lblNotes;
    @FXML Label lblTime;
    @FXML Label lblLocation;
    @FXML
    Label lblCoordinators;
    @FXML Label lblName;

    private UserManager userManager;
    private EventCoordinatorLogic eventCoordinatorLogic;
    private EventLogic eventLogic;
    private SessionManager sessionManager;
    private ObservableList<User> coordinatorList = FXCollections.observableArrayList();

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        this.eventCoordinatorLogic = eventCoordinatorLogic;
        System.out.println("overview is setting" +  eventCoordinatorLogic);
    }
    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void populateEvent(Event selectedEvent) {
        lblName.setText(selectedEvent.getName());
        lblTime.setText("📅 " + selectedEvent.getStartDate().toString());
        lblLocation.setText("📍 "+selectedEvent.getLocation());
        lblCoordinators.setText("👥 " + eventCoordinatorLogic.getCoordinatorIdsForEvent(selectedEvent.getId()).size() + " coordinators assigned");
        lblGuidance.setText(selectedEvent.getLocationDescription());
        lblNotes.setText(selectedEvent.getDescription());

        List<User> coordinators = eventCoordinatorLogic.getCoordinatorsForEvent(selectedEvent.getId());
        ObservableList<String> coordinatorNames = FXCollections.observableArrayList();

        for (User coordinator : coordinators) {
            coordinatorNames.add(coordinator.getName() + " " + coordinator.getSurname());
        }
        lstCoordinators.setItems(coordinatorNames);

    }

    public void handleBackToEvents(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/dk/easv/eventticketapp/gui/adminViews/Events.fxml"
                            )
                    )
            );

            Node node = loader.load();
            EventsController controller = loader.getController();
            controller.setUserManager(userManager);
            controller.setEventLogic(eventLogic);
            controller.setEventCoordinatorLogic(eventCoordinatorLogic);
            controller.setSessionManager(sessionManager);
            controller.init();
            AdminMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


