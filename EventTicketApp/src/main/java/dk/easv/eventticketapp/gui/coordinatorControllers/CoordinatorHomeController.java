package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.SessionManager;
import dk.easv.eventticketapp.bll.TicketTypeManager;
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

public class CoordinatorHomeController {

    @FXML
    private VBox eventContainer;

    @FXML
    private TextField txtFilter;

    private ObservableList<Event> events;
    private FilteredList<Event> filteredEvents;

    private EventCoordinatorLogic eventCoordinatorLogic;
    private EventLogic eventLogic;
    private SessionManager sessionManager;
    private TicketTypeManager ticketTypeManager;

    // ✅ IMPORTANT: reusable click behavior
    private Consumer<Event> onCardClick;

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        this.eventCoordinatorLogic = eventCoordinatorLogic;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setTicketTypeManager(TicketTypeManager ticketTypeManager) {
        this.ticketTypeManager = ticketTypeManager;
    }

    public void setOnCardClick(Consumer<Event> onCardClick) {
        this.onCardClick = onCardClick;
    }

    public void init() {
        loadMyEvents();
    }

    @FXML
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

                // 🔥 PASS EVENT DATA
                EventHeaderController controller = loader.getController();
                controller.setEvent(event);
                controller.setTicketTypeManager(ticketTypeManager);

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
                    eventCoordinatorLogic.getEventsForUser(currentUser.getId())
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

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/EventCard.fxml"
                    )
            );

            try {
                VBox card = loader.load();

                EventCardController controller = loader.getController();

                controller.setEventCoordinatorLogic(eventCoordinatorLogic);
                controller.setEventLogic(eventLogic);
                controller.setEvent(event);

                // ✅ Delegate click behavior (NO hardcoding)
                if (onCardClick != null) {
                    controller.setOnCardClick(onCardClick);
                }

                // ✅ Refresh after delete
                controller.setOnDeleteSuccess(this::loadMyEvents);

                eventContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createEventBtn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditEvent.fxml"
                    )
            );

            Node node = loader.load();
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showVouchers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/VouchersOverview.fxml"
                    )
            );

            Node node = loader.load();
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEvents(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/CoordinatorHome.fxml"
                    )
            );

            Node node = loader.load();
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
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