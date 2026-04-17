package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.gui.coordinatorControllers.TicketController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;


public class IssuedTicketsController implements ApplicationServicesAware {

    @FXML private TableView<IssuedTicket> tblIssuedTickets;
    @FXML private TableColumn<IssuedTicket, String> columnName;
    @FXML private TableColumn<IssuedTicket, String> columnEmail;
    @FXML private TableColumn<IssuedTicket, String> columnTicketType;
    @FXML private TableColumn<IssuedTicket, Integer> columnQuantity;
    @FXML private TableColumn<IssuedTicket, Double> columnTotalPrice;


    private StackPane contentArea;
    private Event currentEvent;
    private IssuedTicket selectedTicket;
    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

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

        tblIssuedTickets.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedTicket = newVal;
        });
    }

    public void loadTickets(int eventId) {
        tblIssuedTickets.getItems().setAll(
                services.getTicketManager().getIssuedTickets(eventId)
        );
    }

    @FXML
    private void onSeeTicket() {
        IssuedTicket selected = tblIssuedTickets.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "No Ticket Selected", "Please select a ticket to open it.");
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

    @FXML
    public void onDeleteTicket(ActionEvent actionEvent) {

        if (selectedTicket == null) {
            showAlert(Alert.AlertType.ERROR,
                    "No Selection",
                    "Please select a ticket to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Ticket");
        confirm.setContentText("Are you sure you want to delete this ticket for "
                + selectedTicket.getCustomerName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    services.getTicketManager().deleteTicket(selectedTicket.getId());

                    loadTickets(currentEvent.getId());

                    // reset after refresh
                    selectedTicket = null;

                    showAlert(Alert.AlertType.INFORMATION,
                            "Success",
                            "Ticket deleted successfully.");

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR,
                            "Error",
                            "Failed to delete ticket.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
