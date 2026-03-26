package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class EventCardController {

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label coordinatorLabel;
    @FXML private Label ticketsLabel;
    @FXML private VBox clickArea; // 🔥 IMPORTANT (was missing)

    private Event event;
    private EventLogic eventLogic;
    private EventCoordinatorLogic eventCoordinatorLogic;
    private Runnable onDeleteSuccess;

    // 🔥 NEW: callback for click
    private Consumer<Event> onCardClick;

    public void setEvent(Event event) {
        this.event = event;

        titleLabel.setText(event.getName());
        locationLabel.setText("📍 " + event.getLocation());

        String formattedDate = event.getStartDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm"));
        dateLabel.setText("📅 " + formattedDate);

        // ✅ FIX coordinator count
        try {
            if (eventCoordinatorLogic != null) {
                int count = eventCoordinatorLogic
                        .getCoordinatorIdsForEvent(event.getId())
                        .size();

                coordinatorLabel.setText("👤 " + count + " coordinator(s)");
            } else {
                coordinatorLabel.setText("👤 0 coordinator(s)");
            }
        } catch (Exception e) {
            coordinatorLabel.setText("👤 error");
        }

        ticketsLabel.setText("0 tickets issued");

        // ✅ FIX click behavior
        if (clickArea != null) {
            clickArea.setOnMouseClicked(e -> {
                if (onCardClick != null) {
                    onCardClick.accept(event);
                }
            });
        }
    }

    public void setOnCardClick(Consumer<Event> onCardClick) {
        this.onCardClick = onCardClick;
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        this.eventCoordinatorLogic = eventCoordinatorLogic;
    }

    public void setOnDeleteSuccess(Runnable onDeleteSuccess) {
        this.onDeleteSuccess = onDeleteSuccess;
    }

    public void handleDelete(ActionEvent actionEvent) {
        if (event == null) {
            System.out.println("no event");
            return;
        }

        try {
            eventCoordinatorLogic.deleteEvent(event);

            if (onDeleteSuccess != null) {
                onDeleteSuccess.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}