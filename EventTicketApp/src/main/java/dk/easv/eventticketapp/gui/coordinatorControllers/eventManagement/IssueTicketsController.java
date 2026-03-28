package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.bll.CustomerLogic;
import dk.easv.eventticketapp.bll.TicketManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class IssueTicketsController {

    @FXML private TextField customerName;
    @FXML private TextField customerEmail;
    @FXML private ComboBox ticketTypeSelection;
    @FXML private TextField ticketQuantity;

    private TicketManager ticketManager = new TicketManager();
    private CustomerLogic customerLogic = new CustomerLogic();

    private Event currentEvent;

    public void setEvent(Event event) {
        this.currentEvent = event;
        //loadTicketTypes();
    }


    public void generateTicket(ActionEvent actionEvent) {

    }
}
