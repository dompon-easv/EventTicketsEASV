package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.be.Customer;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.CustomerLogic;
import dk.easv.eventticketapp.bll.TicketManager;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class IssueTicketsController implements ApplicationServicesAware {

    @FXML private TextField customerName;
    @FXML private TextField customerEmail;
    @FXML private ComboBox<TicketType> ticketTypeSelection;
    @FXML private TextField ticketQuantity;

    private Event currentEvent;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
        loadTicketTypes();
    }

    private void loadTicketTypes() {
        try {
            if (currentEvent == null || services.getTicketTypeManager() == null) return;

            ticketTypeSelection.setItems(
                    services.getTicketTypeManager().getTicketTypesForEvent(currentEvent.getId())
            );

            ticketTypeSelection.setConverter(new StringConverter<>() {
                @Override
                public String toString(TicketType ticketType) {
                    return ticketType == null ? "" : ticketType.getName();
                }

                @Override
                public TicketType fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generateTicket(ActionEvent actionEvent) {
        try {
            String name = customerName.getText().trim();
            String email = customerEmail.getText().trim();
            int quantity = Integer.parseInt(ticketQuantity.getText().trim());

            if (name.isEmpty() || email.isEmpty()) {
                throw new IllegalArgumentException("Customer name and email are required.");
            }

            TicketType selectedType = ticketTypeSelection.getValue();

            if (selectedType == null) {
                throw new IllegalArgumentException("Please select a ticket type.");
            }

            Customer customer = services.getCustomerLogic().createCustomer(name, email);

            services.getTicketManager().issueTicket(
                    quantity,
                    currentEvent.getId(),
                    selectedType.getId(),
                    customer.getId()
            );

            showSuccess("Success", "Ticket(s) issued successfully!");
            clearFields();
            loadTicketTypes();

        } catch (NumberFormatException e) {
            showError("Invalid Input", "Quantity must be a valid number.");
        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to issue ticket.");
        }
    }

    private void clearFields() {
        customerName.clear();
        customerEmail.clear();
        ticketQuantity.clear();
        ticketTypeSelection.getSelectionModel().clearSelection();
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