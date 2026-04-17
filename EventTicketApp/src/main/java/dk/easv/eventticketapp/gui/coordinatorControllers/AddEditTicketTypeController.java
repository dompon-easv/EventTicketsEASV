package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.TicketType;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement.TicketTypesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class AddEditTicketTypeController implements ApplicationServicesAware{

    @FXML private StackPane contentArea;
    @FXML private Label formTitle;
    @FXML private Button saveButton;

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField eventNameField;

    private Event currentEvent;
    private TicketTypesController parentController;
    private TicketType ticketTypeToEdit;
    private boolean isEditMode = false;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

private ApplicationServices services;

@Override
public void setApplicationServices(ApplicationServices services) {
    this.services = services;
}

    public void setEvent(Event event) {
        this.currentEvent = event;
        if (event != null) {
            eventNameField.setText(event.getName());
            eventNameField.setDisable(true);

            if (services.getTicketTypeManager() != null) {
                services.getTicketTypeManager().setCurrentEvent(event);
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
            int soldCount = services.getTicketTypeManager().getSoldTicketsCount(ticketType.getId());
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
            if (services.getTicketTypeManager() == null) {
                throw new Exception("TicketTypeManager not initialized! Please restart the application.");
            }

            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            services.getTicketTypeManager().setCurrentEvent(currentEvent);

            if (isEditMode) {
                ticketTypeToEdit.setName(name);
                ticketTypeToEdit.setDescription(description);
                ticketTypeToEdit.setPrice(price);
                ticketTypeToEdit.setMaxQuantity(quantity);

                services.getTicketTypeManager().updateTicketType(ticketTypeToEdit);
                showSuccess("Success", "Ticket type '" + name + "' has been updated successfully!");
            } else {
                services.getTicketTypeManager().addTicketType(name, description, price, quantity);
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

                FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/eventManagement/TicketTypes.fxml"
                );

                Node view = loader.load();
                TicketTypesController controller = loader.getController();
                controller.setEvent(currentEvent);
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
