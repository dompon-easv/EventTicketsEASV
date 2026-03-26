package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.UserManager;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventCardController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class EventsController {
    @FXML
    TextField txtFilter;
    @FXML
    Label lblEventNumber;
    @FXML
    Label lblCoordinatorsNumber;
    private FilteredList<Event> filteredEvents;
    @FXML VBox eventContainer;
    @FXML Label lblOwner;

    private UserManager userManager;
    private EventLogic eventLogic;
    private EventCoordinatorLogic eventCoordinatorLogic;

    private ObservableList<Event> events;


    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        lblCoordinatorsNumber.setText(String.valueOf(userManager.getCoordinatorCount()));
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
        lblEventNumber.setText(String.valueOf(eventLogic.getEventCount()));
        //loadEvents();
    }

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        System.out.println("events controller got logic:"+eventCoordinatorLogic);
        this.eventCoordinatorLogic = eventCoordinatorLogic;
    }


    public void loadEvents() {

        events = FXCollections.observableArrayList(eventLogic.getAllEvents());
        filteredEvents = new FilteredList<>(events, event -> true);;
        renderEventCards(filteredEvents);
    }

    public void renderEventCards(FilteredList<Event> filteredEvents) {

        eventContainer.getChildren().clear();

        for (Event event : filteredEvents) {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/EventCard.fxml"
                    )
            );

           try{
               VBox card = loader.load();

               EventCardController controller = loader.getController();
               controller.setEventLogic(eventLogic);
               controller.setEventCoordinatorLogic(eventCoordinatorLogic);
               System.out.println("rendering got logic" + eventCoordinatorLogic);
               controller.setEvent(event);
               controller.setOnDeleteSuccess(this::loadEvents);




            eventContainer.getChildren().add(card); }
        catch(Exception e) {e.printStackTrace();}
        }

    }


    public void initialize() {
        filtering();
    }

    public void filtering() {
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEvents.setPredicate(event -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String filter = newValue.toLowerCase().trim();

                return contains(event.getName(), filter)
                        || contains(event.getLocation(), filter)
                        || contains(event.getDescription(), filter);
            });
            renderEventCards(filteredEvents);
        });
    }


    private boolean contains(String text, String filter) {
        return text != null && text.toLowerCase().contains(filter.toLowerCase());
    }

    public void init(){
        loadEvents();
    }
}
