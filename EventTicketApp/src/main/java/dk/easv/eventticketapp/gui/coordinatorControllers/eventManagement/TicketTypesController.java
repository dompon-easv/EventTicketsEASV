package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.AddEditTicketTypeController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class TicketTypesController implements ApplicationServicesAware {

    @FXML private StackPane contentArea;
    @FXML private Button btnAddTicketType;
    @FXML private Button btnEditTicketType;
    @FXML private Button btnDeleteTicketType;

    @FXML private TableView<TicketType> tableView;
    @FXML private TableColumn<TicketType, String> columnName;
    @FXML private TableColumn<TicketType, String> columnDescription;
    @FXML private TableColumn<TicketType, Double> columnPrice;
    @FXML private TableColumn<TicketType, Integer> columnQuantity;
    @FXML private TableColumn<TicketType, Integer> columnSold;
    @FXML private TableColumn<TicketType, Integer> columnAvailability;

    private Event currentEvent;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSelectionHandling();
        disableActionButtons();
    }

    private void setupTableColumns() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("maxQuantity"));
        columnSold.setCellValueFactory(cellData -> {
            try {
                int sold = services.getTicketTypeManager().getSoldTicketsCount(cellData.getValue().getId());
                return new SimpleIntegerProperty(sold).asObject();
            } catch (Exception e) {
                e.printStackTrace();
                return new SimpleIntegerProperty(0).asObject();
            }
        });
        columnAvailability.setCellValueFactory(cellData -> {
            TicketType tt = cellData.getValue();

            try {
                int sold = services.getTicketTypeManager().getSoldTicketsCount(tt.getId());
                int remaining = tt.getMaxQuantity() - sold;

                return new SimpleIntegerProperty(remaining).asObject();
            } catch (Exception e) {
                e.printStackTrace();
                return new SimpleIntegerProperty(0).asObject();
            }
        });
    }

    private void setupSelectionHandling() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean isSelected = newSel != null;
            btnEditTicketType.setDisable(!isSelected);
            btnDeleteTicketType.setDisable(!isSelected);
        });

        tableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    Object target = event.getTarget();
                    if (target instanceof Node node) {
                        if (!isInsideTable(node) && !isButton(node)) {
                            tableView.getSelectionModel().clearSelection();
                        }
                    }
                });
            }
        });
    }

    private boolean isInsideTable(Node node) {
        while (node != null) {
            if (node == tableView) return true;
            node = node.getParent();
        }
        return false;
    }

    private boolean isButton(Node node) {
        while (node != null) {
            if (node instanceof Button) return true;
            node = node.getParent();
        }
        return false;
    }

    private void disableActionButtons() {
        btnEditTicketType.setDisable(true);
        btnDeleteTicketType.setDisable(true);
    }

    //------------Setters------------

    public void setTicketTypeManager(TicketTypeManager manager) {
        tryLoadData();
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        tryLoadData();

        if (services.getTicketTypeManager() != null && currentEvent != null) {
            services.getTicketTypeManager().setCurrentEvent(currentEvent);
            loadTicketTypes();
        }
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public StackPane getContentArea() {
        return contentArea;
    }


    //-------------Data Loading-----------

    private void tryLoadData() {
        if (services.getTicketTypeManager() != null && currentEvent != null) {
            try {
                services.getTicketTypeManager().setCurrentEvent(currentEvent);
                tableView.setItems(
                        services.getTicketTypeManager().getTicketTypesForEvent(currentEvent.getId())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTicketTypes() {
        try {
            if(currentEvent == null) return;
            ObservableList<TicketType> list = services.getTicketTypeManager().getTicketTypesForEvent(currentEvent.getId());
            tableView.setItems(list);
            checkCapacityStatus();
            tableView.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //-----------Actions-----------

    public void onAddTicketType(ActionEvent actionEvent) {
        try {
            if (currentEvent == null) {
                System.err.println("ERROR: No event selected!");
                return;
            }

            if (services.getTicketTypeManager() == null) {
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
            controller.setParentController(this);
            controller.setContentArea(contentArea);

            contentArea.getChildren().setAll(node);

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

            if (services.getTicketTypeManager() == null) {
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
            controller.setTicketTypeToEdit(selectedTicket);
            controller.setParentController(this);
            controller.setContentArea(contentArea);

            contentArea.getChildren().setAll(node);

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
                    services.getTicketTypeManager().deleteTicketType(selectedTicket.getId());
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

    //--------Helpers--------

    public void onClearSelection(ActionEvent actionEvent) {
        tableView.getSelectionModel().clearSelection();
    }

    public void refreshTicketTypes() {
        loadTicketTypes();
    }

    private void checkCapacityStatus() {
        if (currentEvent == null || services.getTicketTypeManager() == null) return;

        try {
            if (!services.getTicketTypeManager().isTotalCapacityValid(currentEvent.getId())) {
                String summary = services.getTicketTypeManager().getCapacitySummary(currentEvent.getId());
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

    //------------Alerts-----------

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