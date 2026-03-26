package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.UserManager;
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

    private UserManager userManager;
    private EventLogic eventLogic;

    private ObservableList<Event> events;


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

            Label title = (Label) card.lookup("#titleLabel");
            Label date = (Label) card.lookup("#dateLabel");
            Label location = (Label) card.lookup("#locationLabel");
            Label coordinator = (Label) card.lookup("#coordinatorLabel");
            Label tickets = (Label) card.lookup("#ticketsLabel");
            VBox clickArea = (VBox) card.lookup("#clickArea");

            title.setText(event.getName());

            String formattedDate = event.getStartDate()
                    .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm"));
            date.setText("📅 " + formattedDate);

            location.setText("📍 " + event.getLocation());

            //int count = filteredEvents.size();
            // coordinator.setText("👤 " + count + " coordinator(s)");

            tickets.setText("0 tickets issued");

            //clickArea.setOnMouseClicked(e -> {
             //   selectedEvent = event;
             //   openEvent(e);


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
}
