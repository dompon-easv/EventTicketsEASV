package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class EventCardController {
    @FXML
    private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label coordinatorLabel;
    @FXML private Label ticketsLabel;


    private Event event;
    private EventLogic eventLogic;
    private Runnable onDeleteSuccess;
    private EventCoordinatorLogic eventCoordinatorLogic;

    public void setEvent (Event event)
    {
        this.event = event;
        titleLabel.setText(event.getName());
        locationLabel.setText("📍 " + event.getLocation());

        String formattedDate = event.getStartDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm"));
        dateLabel.setText("📅 " + formattedDate);


       // int count = filteredEvents.size();
        coordinatorLabel.setText("👤 " + " coordinator(s)");

        ticketsLabel.setText("0 tickets issued");

        //clickArea.setOnMouseClicked(e -> {
        //   selectedEvent = event;
        //   openEvent(e);


    }

    public void setEventLogic(EventLogic eventLogic)
    {
        this.eventLogic = eventLogic;
    }

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic){
        this.eventCoordinatorLogic = eventCoordinatorLogic;
    }

    public void setOnDeleteSuccess(Runnable onDeleteSuccess)
    {
        this.onDeleteSuccess = onDeleteSuccess;
    }

    public void handleDelete(ActionEvent actionEvent) {
        if (event == null)
        {
            System.out.println("no event");
            return;
        }
        try{
            eventCoordinatorLogic.deleteEvent(event);

            if (onDeleteSuccess != null){
                onDeleteSuccess.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
