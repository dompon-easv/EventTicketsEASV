package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TicketController {

    @FXML private Label lblEvent;
    @FXML private Label lblTicketType;
    @FXML private Label lblName;
    @FXML private Label lblLocation;
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblQuantity;
    @FXML private Label lblPrice;

    public void setTicket(IssuedTicket ticket, Event event) {

        lblName.setText(ticket.getCustomerName());
        lblTicketType.setText(ticket.getTicketType());
        lblQuantity.setText(String.valueOf(ticket.getQuantity()));
        lblPrice.setText(String.format("kr. %.2f", ticket.getPrice()));

        lblEvent.setText(event.toString());
        lblLocation.setText(event.getLocation());
        lblDate.setText(event.getStartDate().toLocalDate().toString());
        lblTime.setText(event.getStartDate().toLocalTime().toString());    }

}
