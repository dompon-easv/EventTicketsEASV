package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventCardController;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventHeaderController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Consumer;

public class CoordinatorHomeController implements ApplicationServicesAware {

    @FXML
    private VBox eventContainer;

    @FXML
    private TextField txtFilter;

    private ObservableList<Event> events;
    private FilteredList<Event> filteredEvents;

    private CoordinatorMainController coordinatorMainController;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    // ✅ IMPORTANT: reusable click behavior
    private Consumer<Event> onCardClick;


    public void setOnCardClick(Consumer<Event> onCardClick) {
        this.onCardClick = onCardClick;
    }
    public void setMainCoordinatorController(CoordinatorMainController coordinatorMainController) {
        this.coordinatorMainController = coordinatorMainController;
    }

    public void init() {
        loadMyEvents();
    }

    @FXML
    public void initialize() {
        filtering();

        setOnCardClick(event -> {
            try {
                FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/EventHeader.fxml"
                );

                Node node = loader.load();
                EventHeaderController controller = loader.getController();
                controller.setEvent(event);

                controller.setCoordinatorMainController(coordinatorMainController);

                CoordinatorMainController.staticContentArea.getChildren().setAll(node);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadMyEvents() {
        try {
            User currentUser = SessionManager.getCurrentUser();

            events = FXCollections.observableArrayList(
                    services.getEventCoordinatorLogic().getEventsForUser(currentUser.getId())
            );

            filteredEvents = new FilteredList<>(events, e -> true);

            renderEventCards(filteredEvents);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderEventCards(FilteredList<Event> filteredEvents) {

        eventContainer.getChildren().clear();

        for (Event event : filteredEvents) {

            FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/EventCard.fxml"
            );

            try {
                VBox card = loader.load();

                EventCardController controller = loader.getController();

                controller.setEvent(event);

                if (onCardClick != null) {
                    controller.setOnCardClick(onCardClick);
                }
                controller.setOnDeleteSuccess(this::loadMyEvents);

                eventContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createEventBtn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/AddEditEvent.fxml"
            );

            Node node = loader.load();
            AddEditEventController controller = loader.getController();
            controller.setCoordinatorMainController(coordinatorMainController);
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showVouchers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/VouchersOverview.fxml"
            );
            Node node = loader.load();

            VouchersOverviewController controller = loader.getController();
            controller.setMainCoordinatorController(coordinatorMainController);
            controller.initData();

            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEvents(ActionEvent actionEvent) {
        coordinatorMainController.loadView("CoordinatorHome.fxml");

    }

    public void filtering() {
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {

            if (filteredEvents == null) return;

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
        return text != null && text.toLowerCase().contains(filter);
    }
}