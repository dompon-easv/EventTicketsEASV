package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.AddEditTicketTypeController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class TicketTypesController {

    public Button btnAddTicketType;
    public Button btnEditTicketType;
    public Button btnDeleteTicketType;
    public Button btnClearSelection;

    public TableView<TicketType> tableView;
    public TableColumn<TicketType, String> columnName;
    public TableColumn<TicketType, String> columnDescription;
    public TableColumn<TicketType, Double> columnPrice;
    public TableColumn<TicketType, Integer> columnQuantity;
    public TableColumn<TicketType, Integer> columnSold;
    public TableColumn<TicketType, Integer> columnAvailability;

    private TicketTypeManager ticketTypeManager;
    private TicketManager ticketManager;
    private CustomerLogic customerLogic;
    private EventCoordinatorLogic eventCoordinatorLogic;
    private EventLogic eventLogic;
    private UserManager userManager;
    private SessionManager sessionManager;
    private Event currentEvent;

    @FXML
    public void initialize() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("maxQuantity"));
        columnSold.setCellValueFactory(new PropertyValueFactory<>("ticketsSold"));
        columnAvailability.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        /*tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            btnEditTicketType.setDisable(!isSelected);
            btnDeleteTicketType.setDisable(!isSelected);
            btnClearSelection.setDisable(!isSelected);
        });*/

        tableView.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // newVal is false when focus is lost
                tableView.getSelectionModel().clearSelection();
            }
        });

        btnEditTicketType.setDisable(true);
        btnDeleteTicketType.setDisable(true);
        btnClearSelection.setDisable(true);
    }

    public void setTicketTypeManager(TicketTypeManager manager) {
        this.ticketTypeManager = manager;
        tryLoadData();
    }

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public void setCustomerLogic(CustomerLogic customerLogic) {
        this.customerLogic = customerLogic;
    }

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        this.eventCoordinatorLogic = eventCoordinatorLogic;
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public CustomerLogic getCustomerLogic() {
        return customerLogic;
    }

    public EventCoordinatorLogic getEventCoordinatorLogic() {
        return eventCoordinatorLogic;
    }

    public EventLogic getEventLogic() {
        return eventLogic;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        tryLoadData();

        if (ticketTypeManager != null && currentEvent != null) {
            ticketTypeManager.setCurrentEvent(currentEvent);
            loadTicketTypes();
        }
    }

    private void tryLoadData() {
        if (ticketTypeManager != null && currentEvent != null) {
            try {
                ticketTypeManager.setCurrentEvent(currentEvent);
                tableView.setItems(
                        ticketTypeManager.getTicketTypesForEvent(currentEvent.getId())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTicketTypes() {
        try {
            if(currentEvent == null) return;
            ObservableList<TicketType> list = ticketTypeManager.getTicketTypesForEvent(currentEvent.getId());
            tableView.setItems(list);
            checkCapacityStatus();
            tableView.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onAddTicketType(ActionEvent actionEvent) {
        try {
            if (currentEvent == null) {
                System.err.println("ERROR: No event selected!");
                return;
            }

            if (ticketTypeManager == null) {
                System.err.println("ERROR: TicketTypeManager not initialized!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditTicketTypes.fxml")
            );

            Node node = loader.load();

            AddEditTicketTypeController controller = loader.getController();
            controller.setEvent(currentEvent);
            controller.setTicketTypeManager(ticketTypeManager);
            controller.setParentController(this);
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEditTicketType(ActionEvent actionEvent) {
        TicketType selectedTicket = tableView.getSelectionModel().getSelectedItem();

        if (selectedTicket == null) {
            showError("No Selection", "Please select a ticket type to edit.");
            return;
        }

        try {
            if (currentEvent == null) {
                System.err.println("ERROR: No event selected!");
                return;
            }

            if (ticketTypeManager == null) {
                System.err.println("ERROR: TicketTypeManager not initialized!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/AddEditTicketTypes.fxml")
            );

            Node node = loader.load();

            AddEditTicketTypeController controller = loader.getController();
            controller.setEvent(currentEvent);
            controller.setTicketTypeManager(ticketTypeManager);
            controller.setTicketTypeToEdit(selectedTicket);
            controller.setParentController(this);

            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Could not open edit form: " + e.getMessage());
        }
    }

    public void onDeleteTicketType(ActionEvent actionEvent) {
        TicketType selectedTicket = tableView.getSelectionModel().getSelectedItem();

        if (selectedTicket == null) {
            showError("No Selection", "Please select a ticket type to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Ticket Type");
        confirmDialog.setContentText("Are you sure you want to delete '" + selectedTicket.getName() + "'?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ticketTypeManager.deleteTicketType(selectedTicket.getId());
                    loadTicketTypes();
                    showSuccess("Success", "Ticket type '" + selectedTicket.getName() + "' has been deleted successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("ticket(s) have already been sold")) {
                        showError("Cannot Delete", e.getMessage());
                    } else {
                        showError("Error", "Failed to delete ticket type: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void onClearSelection(ActionEvent actionEvent) {
        tableView.getSelectionModel().clearSelection();
    }

    public void refreshTicketTypes() {
        loadTicketTypes();
    }

    private void checkCapacityStatus() {
        if (currentEvent == null || ticketTypeManager == null) return;

        try {
            if (!ticketTypeManager.isTotalCapacityValid(currentEvent.getId())) {
                String summary = ticketTypeManager.getCapacitySummary(currentEvent.getId());
                showWarning("Capacity Exceeded",
                        "Warning: Total ticket type quantities exceed event capacity!\n\n" + summary +
                                "\n\nYou should either:\n" +
                                "• Reduce some ticket type quantities, or\n" +
                                "• Increase the event capacity in event settings."
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}