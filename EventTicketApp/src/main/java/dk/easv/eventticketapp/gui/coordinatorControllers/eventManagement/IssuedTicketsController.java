package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.bll.TicketManager;
import dk.easv.eventticketapp.dao.TicketDAO;
import dk.easv.eventticketapp.dao.TicketTypeDAO;
import dk.easv.eventticketapp.gui.coordinatorControllers.TicketController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;


public class IssuedTicketsController {

    @FXML private Button btnSeeTicket;
    @FXML private TableView<IssuedTicket> tblIssuedTickets;
    @FXML private TableColumn<IssuedTicket, String> columnName;
    @FXML private TableColumn<IssuedTicket, String> columnEmail;
    @FXML private TableColumn<IssuedTicket, String> columnTicketType;
    @FXML private TableColumn<IssuedTicket, Integer> columnQuantity;
    @FXML private TableColumn<IssuedTicket, Double> columnTotalPrice;

    private final TicketManager ticketManager =
            new TicketManager(new TicketDAO(), new TicketTypeDAO());

    private StackPane contentArea;
    private Event currentEvent;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
    }

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

        columnTotalPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        columnTotalPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("kr. %.2f", value));
                }
            }
        });
    }

    public void loadTickets(int eventId) {
        tblIssuedTickets.getItems().setAll(
                ticketManager.getIssuedTickets(eventId)
        );
    }

    @FXML
    private void handleEditEvent() {
        IssuedTicket selected = tblIssuedTickets.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No ticket selected");
            return;
        }
        openTicketView(selected);
    }

    private void openTicketView(IssuedTicket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/dk/easv/eventticketapp/gui/coordinatorViews/Ticket.fxml"
            ));

            Node view = loader.load();

            TicketController controller = loader.getController();
            controller.setTicket(ticket, currentEvent);

            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
