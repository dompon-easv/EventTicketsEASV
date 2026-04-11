package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.EventHeaderController;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.TicketTypesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AddEditTicketTypeController {

    @FXML private StackPane contentArea;
    @FXML private Label formTitle;
    @FXML private Button saveButton;

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField eventNameField;

    private TicketTypeManager ticketTypeManager;
    private Event currentEvent;
    private TicketTypesController parentController;
    private TicketType ticketTypeToEdit;
    private boolean isEditMode = false;
    private TicketManager ticketManager;
    private CustomerLogic customerLogic;
    private EventCoordinatorLogic eventCoordinatorLogic;
    private EventLogic eventLogic;
    private UserManager userManager;
    private SessionManager sessionManager;

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public void setCustomerLogic(CustomerLogic customerLogic) {
        this.customerLogic = customerLogic;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
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
        }
    }

    public void setParentController(TicketTypesController parentController) {
        this.parentController = parentController;
    }

    public void setTicketTypeToEdit(TicketType ticketType) {
        this.ticketTypeToEdit = ticketType;
        this.isEditMode = true;

        formTitle.setText("Edit Ticket Type");
        saveButton.setText("Update Ticket Type");

        if (ticketType != null) {
            nameField.setText(ticketType.getName());
            descriptionField.setText(ticketType.getDescription());
            priceField.setText(String.valueOf(ticketType.getPrice()));
            quantityField.setText(String.valueOf(ticketType.getMaxQuantity()));
        }

        try {
            int soldCount = ticketTypeManager.getSoldTicketsCount(ticketType.getId());
            if (soldCount > 0) {
                showInfo("Note", String.format(
                        "This ticket type already has %d ticket(s) sold.\n" +
                                "You cannot reduce the quantity below this number.",
                        soldCount
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            if (isEditMode) {
                ticketTypeToEdit.setName(name);
                ticketTypeToEdit.setDescription(description);
                ticketTypeToEdit.setPrice(price);
                ticketTypeToEdit.setMaxQuantity(quantity);

                ticketTypeManager.updateTicketType(ticketTypeToEdit);
                showSuccess("Success", "Ticket type '" + name + "' has been updated successfully!");
            } else {
                ticketTypeManager.addTicketType(name, description, price, quantity);
                showSuccess("Success", "Ticket type '" + name + "' has been created successfully!");
            }

            if(parentController != null) {
                parentController.refreshTicketTypes();
            }

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
        System.out.println("Close button clicked");

        try {
            if (parentController != null && parentController.getContentArea() != null) {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/TicketTypes.fxml"
                        )
                );

                Node view = loader.load();
                TicketTypesController controller = loader.getController();
                controller.setEvent(currentEvent);
                controller.setTicketTypeManager(ticketTypeManager);
                controller.setTicketManager(parentController.getTicketManager());
                controller.setCustomerLogic(parentController.getCustomerLogic());
                controller.setEventCoordinatorLogic(parentController.getEventCoordinatorLogic());
                controller.setEventLogic(parentController.getEventLogic());
                controller.setUserManager(parentController.getUserManager());
                controller.setSessionManager(parentController.getSessionManager());
                controller.setContentArea(parentController.getContentArea());

                parentController.getContentArea().getChildren().setAll(view);

            } else {
                System.out.println("Fallback navigation");

                CoordinatorMainController mainController =
                        (CoordinatorMainController) nameField.getScene().getWindow().getUserData();

                mainController.loadView("CoordinatorHome.fxml");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not return to ticket types.");
        }
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
}