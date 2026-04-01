package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.bll.TicketManager;
import dk.easv.eventticketapp.dao.TicketDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Collections;

public class IssuedTicketsController {

    @FXML private TableView<IssuedTicket> tblIssuedTickets;
    @FXML private TableColumn<IssuedTicket, String> columnName;
    @FXML private TableColumn<IssuedTicket, String> columnEmail;
    @FXML private TableColumn<IssuedTicket, String> columnTicketType;
    @FXML private TableColumn<IssuedTicket, Integer> columnQuantity;

    private final TicketManager ticketManager = new TicketManager(new TicketDAO());

    @FXML
    public void initialize() {

        columnName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerName()));

        columnEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));

        columnTicketType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTicketType()));

        columnQuantity.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
    }

    public void loadTickets(int eventId) {
        tblIssuedTickets.getItems().setAll(
                ticketManager.getIssuedTickets(eventId)
        );
    }
}
