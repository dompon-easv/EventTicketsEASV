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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class TicketTypesController {

    public Button btnAddTicketType;
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

            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEditTicketType(ActionEvent actionEvent) {
        // Implement edit functionality
    }

    public void onDeleteTicketType(ActionEvent actionEvent) {
        // Implement delete functionality
    }
}