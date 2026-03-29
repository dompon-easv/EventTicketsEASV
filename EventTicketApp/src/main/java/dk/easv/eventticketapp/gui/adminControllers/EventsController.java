package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventCardController;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventHeaderController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

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
    private SessionManager sessionManager;
    private Consumer<Event> onCardClick;


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
               controller.setEvent(event);
               if (onCardClick != null) {
                   controller.setOnCardClick(onCardClick);
               }
               controller.setOnDeleteSuccess(this::loadEvents);




            eventContainer.getChildren().add(card); }
        catch(Exception e) {e.printStackTrace();}
        }

    }


    public void initialize() {
        filtering();
        setOnCardClick(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/EventHeader.fxml"
                        )
                );

                Node node = loader.load();

                EventHeaderController controller = loader.getController();
                controller.setEvent(event);
                controller.setEventCoordinatorLogic(eventCoordinatorLogic);
                controller.setEventLogic(eventLogic);
                controller.setUserManager(userManager);
                controller.setSessionManager(sessionManager);
                //controller.setTicketTypeManager(ticketTypeManager);
                controller.init();

                AdminMainController.staticContentArea.getChildren().setAll(node);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setOnCardClick(Consumer<Event> onCardClick) {
        this.onCardClick = onCardClick;
    }

}
