package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventHeaderController;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.TicketTypesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class AddEditTicketTypeController {

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField eventNameField;

    private TicketTypeManager ticketTypeManager;
    private Event currentEvent;
    private Consumer<TicketType> onTicketCreated;


    public void setTicketTypeManager(TicketTypeManager manager) {
        this.ticketTypeManager = manager;
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        if (event != null) {
            eventNameField.setText(event.getName());
            eventNameField.setDisable(true);

            if (ticketTypeManager != null) {
                ticketTypeManager.setCurrentEvent(event);
            }
        } else {
            System.err.println("ERROR: Event is null in AddEditTicketTypeController.setEvent()");
        }
    }

    public void setOnTicketCreated(Consumer<TicketType> callback) {
        this.onTicketCreated = callback;
    }

    @FXML
    public void onSaveTicketType(ActionEvent actionEvent) {
        try {
            if (ticketTypeManager == null) {
                throw new Exception("TicketTypeManager not initialized! Please restart the application.");
            }

            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            ticketTypeManager.setCurrentEvent(currentEvent);
            TicketType saved = ticketTypeManager.addTicketType(name, description, price, quantity);

            if(onTicketCreated != null) {
                onTicketCreated.accept(saved);
            }

            showSuccess("Success", "Ticket type '" + name + "' has been created successfully!");
            closeBtn(actionEvent);

        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter valid numbers for price and quantity.");
        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to save ticket: " + e.getMessage());
        }
    }

    @FXML
    public void closeBtn(ActionEvent actionEvent) {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/CoordinatorMain.fxml"));

            Parent mainRoot = mainLoader.load();
            CoordinatorMainController mainController = mainLoader.getController();

            FXMLLoader headerLoader = new FXMLLoader(getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/EventHeader.fxml"));

            Node headerView = headerLoader.load();
            EventHeaderController headerController = headerLoader.getController();

            headerController.setEvent(currentEvent);
            headerController.setTicketTypeManager(ticketTypeManager);

            mainController.contentArea.getChildren().setAll(headerView);

            FXMLLoader ticketTypesLoader = new FXMLLoader(getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/TicketTypes.fxml"));

            Node ticketTypesView = ticketTypesLoader.load();
            TicketTypesController ticketTypesController = ticketTypesLoader.getController();
            ticketTypesController.setTicketTypeManager(ticketTypeManager);
            ticketTypesController.setEvent(currentEvent);

            headerController.contentArea.getChildren().setAll(ticketTypesView);
            headerController.btnTicketTypes.getStyleClass().add("active");
            headerController.btnOverview.getStyleClass().remove("active");
            headerController.btnIssueTickets.getStyleClass().remove("active");
            headerController.btnIssuedTickets.getStyleClass().remove("active");

            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not return to event details: " + e.getMessage());
        }
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