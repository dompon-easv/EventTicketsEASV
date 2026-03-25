package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CoordinatorHomeController {

    private Event selectedEvent;

    @FXML
    private VBox eventContainer;

    @FXML
    public void initialize() {
        loadMyEvents();
    }

    private void loadMyEvents() {
        try {
            User currentUser = SessionManager.getCurrentUser();

            EventCoordinatorLogic logic = new EventCoordinatorLogic();
            List<Event> events = logic.getEventsForUser(currentUser.getId());

            eventContainer.getChildren().clear();

            for (Event event : events) {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/EventCard.fxml"
                        )
                );

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

                int count = logic.getCoordinatorIdsForEvent(event.getId()).size();
                coordinator.setText("👤 " + count + " coordinator(s)");

                tickets.setText("0 tickets issued");

                clickArea.setOnMouseClicked(e -> {
                    selectedEvent = event;
                    openEvent(e);
                });

                eventContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openEvent(MouseEvent event) {
        try {
            if (selectedEvent == null) {
                System.out.println("No event selected!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditEvent.fxml"
                    )
            );

            Node node = loader.load();

            AddEditEventController controller = loader.getController();
            controller.populateEvent(selectedEvent);

            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
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
}