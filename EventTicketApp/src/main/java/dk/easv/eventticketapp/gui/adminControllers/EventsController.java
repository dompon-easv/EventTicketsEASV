package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class EventsController {
    @FXML
    TextField txtFilter;
    @FXML
    Label lblEventNumber;
    @FXML
    Label lblCoordinatorsNumber;
    @FXML
    ListView<Event> lstEvents;
    private FilteredList<Event> filteredEvents;

    private UserManager userManager;
    private EventLogic eventLogic;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        lblCoordinatorsNumber.setText(String.valueOf(userManager.getCoordinatorCount()));
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
        lblEventNumber.setText(String.valueOf(eventLogic.getEventCount()));
        loadEvents();
    }


    public void loadEvents() {
        System.out.println("eventLogic = " + eventLogic);
        System.out.println("allEvents = " + eventLogic.getAllEvents());
        ObservableList<Event> events = FXCollections.observableArrayList();
        events.addAll(eventLogic.getAllEvents());
        filteredEvents = new FilteredList<>(events, event -> true);
        lstEvents.setItems(filteredEvents);
    }

    public void initialize() {
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEvents.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filter = newValue.toLowerCase().trim();
                return contains(event.getName(), filter)
                        || contains(event.getLocation(), filter)
                        || contains(event.getDescription(), filter);

            });
        });
    }

    private boolean contains(String text, String filter) {
        return text != null && text.toLowerCase().contains(filter.toLowerCase());
    }
}
