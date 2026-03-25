package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class EventsController {
    @FXML Label lblEventNumber;
    @FXML Label lblCoordinatorsNumber;
    @FXML ListView <String> lstEvents;

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

    public void loadEvents()
    {
        System.out.println("eventLogic = " + eventLogic);
        System.out.println("allEvents = " + eventLogic.getAllEvents());
        ObservableList<String> events = FXCollections.observableArrayList();
        for( Event event : eventLogic.getAllEvents())
        {
            events.add(event.getName());
        }
        lstEvents.setItems(events);
    }




}
