package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.*;
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

public class AdminEventOverviewController implements ApplicationServicesAware {
    public ListView lstCoordinators;
    @FXML Label lblGuidance;
    @FXML
    Label lblNotes;
    @FXML Label lblTime;
    @FXML Label lblLocation;
    @FXML
    Label lblCoordinators;
    @FXML Label lblName;

    private ObservableList<User> coordinatorList = FXCollections.observableArrayList();

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }
    public void populateEvent(Event selectedEvent) {
        lblName.setText(selectedEvent.getName());
        lblTime.setText("📅 " + selectedEvent.getStartDate().toString());
        lblLocation.setText("📍 "+selectedEvent.getLocation());
        lblCoordinators.setText("👥 " + services.getEventCoordinatorLogic().getCoordinatorIdsForEvent(selectedEvent.getId()).size() + " coordinators assigned");
        lblGuidance.setText(selectedEvent.getLocationDescription());
        lblNotes.setText(selectedEvent.getDescription());

        List<User> coordinators = services.getEventCoordinatorLogic().getCoordinatorsForEvent(selectedEvent.getId());
        ObservableList<String> coordinatorNames = FXCollections.observableArrayList();

        for (User coordinator : coordinators) {
            coordinatorNames.add(coordinator.getName() + " " + coordinator.getSurname());
        }
        lstCoordinators.setItems(coordinatorNames);

    }

    public void handleBackToEvents(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/adminViews/Events.fxml"


            );

            Node node = loader.load();
            EventsController controller = loader.getController();
            controller.init();
            AdminMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


