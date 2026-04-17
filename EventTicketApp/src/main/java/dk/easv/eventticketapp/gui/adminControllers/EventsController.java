package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventCardController;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventHeaderController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.function.Consumer;

public class EventsController implements ApplicationServicesAware{
    @FXML
    TextField txtFilter;
    @FXML
    Label lblEventNumber;
    @FXML
    Label lblCoordinatorsNumber;
    private FilteredList<Event> filteredEvents;
    @FXML VBox eventContainer;
    @FXML Label lblOwner;


    private ObservableList<Event> events;
    private Consumer<Event> onCardClick;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    public void loadEvents() {
        events = FXCollections.observableArrayList(services.getEventLogic().getAllEvents());
        filteredEvents = new FilteredList<>(events, event -> true);

        lblEventNumber.setText(String.valueOf(events.size()));

        int coordinatorCount = services.getUserManager().getAllUsers().stream()
                .filter(user -> user.getRole() == dk.easv.eventticketapp.be.enums.UserRole.COORDINATOR)
                .toArray().length;

        lblCoordinatorsNumber.setText(String.valueOf(coordinatorCount));

        renderEventCards(filteredEvents);
    }

    public void renderEventCards(FilteredList<Event> filteredEvents) {

        eventContainer.getChildren().clear();

        for (Event event : filteredEvents) {

            FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/EventCard.fxml");


           try{
               VBox card = loader.load();

               EventCardController controller = loader.getController();
               controller.setEvent(event);
               if (onCardClick != null) {
                   controller.setOnCardClick(onCardClick);
               }
               controller.setOnDeleteSuccess(this::loadEvents);




            eventContainer.getChildren().add(card);
               }

        catch(Exception e) {e.printStackTrace();}
        }

    }


    public void initialize() {
        filtering();
        setOnCardClick(event -> {
            try {
                FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/EventHeader.fxml"

                );

                Node node = loader.load();

                EventHeaderController controller = loader.getController();
                controller.setEvent(event);
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


    public void setOnCardClick(Consumer<Event> onCardClick) {
        this.onCardClick = onCardClick;
    }

}
