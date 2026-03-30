package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import dk.easv.eventticketapp.gui.coordinatorControllers.AddEditTicketTypeController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

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

    private TicketTypeManager ticketTypeManager;
    private Event currentEvent;

    @FXML
    public void initialize() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantityAvailable"));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            btnEditTicketType.setDisable(!isSelected);
            btnDeleteTicketType.setDisable(!isSelected);
            btnClearSelection.setDisable(!isSelected);
        });

        btnEditTicketType.setDisable(true);
        btnDeleteTicketType.setDisable(true);
        btnClearSelection.setDisable(true);
    }

    public void setTicketTypeManager(TicketTypeManager manager) {
        this.ticketTypeManager = manager;
        tryLoadData();
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
            ObservableList<TicketType> list =
                    ticketTypeManager.getTicketTypesForEvent(currentEvent.getId());
            tableView.setItems(list);
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
            controller.setTicketTypeToEdit(selectedTicket); // Pass the ticket to edit
            controller.setParentController(this); // Set parent to refresh after edit

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
                    showError("Error", "Failed to delete ticket type: " + e.getMessage());
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