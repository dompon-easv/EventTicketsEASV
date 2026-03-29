package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEditTicketTypeController {

    @FXML
    private Label lblAddEditInfo;

    @FXML
    private Label lblAddEdit;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField eventNameField;

    private TicketTypeManager ticketTypeManager;
    private Event currentEvent;

    public void setTicketTypeManager(TicketTypeManager manager) {
        this.ticketTypeManager = manager;
        System.out.println("TicketTypeManager set in AddEditTicketTypeController: " + (manager != null ? "not null" : "null"));
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        if (event != null) {
            eventNameField.setText(event.getName());
            eventNameField.setDisable(true);
            System.out.println("Event set in AddEditTicketTypeController: " + event.getName());

            // Update the current event in the manager
            if (ticketTypeManager != null) {
                ticketTypeManager.setCurrentEvent(event);
                System.out.println("Current event set in TicketTypeManager from AddEditTicketTypeController: " + event.getName());
            }
        } else {
            System.err.println("ERROR: Event is null in AddEditTicketTypeController.setEvent()");
        }
    }

    @FXML
    public void closeBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onSaveTicketType(ActionEvent actionEvent) {
        try {
            // Debug output
            System.out.println("=== SAVING TICKET TYPE ===");
            System.out.println("ticketTypeManager is " + (ticketTypeManager == null ? "null" : "not null"));
            System.out.println("currentEvent is " + (currentEvent == null ? "null" : currentEvent.getName()));

            if (ticketTypeManager == null) {
                throw new Exception("TicketTypeManager not initialized! Please restart the application.");
            }

            if (currentEvent == null) {
                throw new Exception("No event selected! Please go back and select an event first.");
            }

            if (!validateInputs()) {
                return;
            }

            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            System.out.println("Saving ticket type for event: " + currentEvent.getName());
            System.out.println("Ticket details - Name: " + name + ", Price: " + price + ", Quantity: " + quantity);

            // Make sure the current event is set in the manager before saving
            ticketTypeManager.setCurrentEvent(currentEvent);

            // Add the ticket type
            ticketTypeManager.addTicketType(name, price, quantity);

            System.out.println("Ticket type saved successfully!");
            showSuccess("Success", "Ticket type '" + name + "' has been created successfully!");
            closeBtn(actionEvent);

        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter valid numbers for price and quantity.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to save ticket: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("- Ticket name is required\n");
        }

        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            errors.append("- Price is required\n");
        } else {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    errors.append("- Price cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Price must be a valid number\n");
            }
        }

        if (quantityField.getText() == null || quantityField.getText().trim().isEmpty()) {
            errors.append("- Quantity is required\n");
        } else {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity < 0) {
                    errors.append("- Quantity cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Quantity must be a valid number\n");
            }
        }

        if (currentEvent == null && (eventNameField.getText() == null || eventNameField.getText().trim().isEmpty())) {
            errors.append("- Event information is missing\n");
        }

        if (errors.length() > 0) {
            showError("Validation Error", errors.toString());
            return false;
        }

        return true;
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