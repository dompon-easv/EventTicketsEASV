package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.bll.EventLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.bll.VoucherLogic;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert;

import java.util.List;

public class AddVoucherController {

    private EventLogic eventLogic;

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<DiscountType> comboDiscountType;
    @FXML private TextField txtDiscountValue;
    @FXML private ToggleButton toggleAllEvents;
    @FXML private ComboBox<Event> comboEvents;

    private VoucherLogic voucherLogic = new VoucherLogic();

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }

    @FXML
    public void initialize() {

        comboDiscountType.getItems().setAll(DiscountType.values());
        comboDiscountType.setOnAction(e -> handleDiscountTypeChange());
        toggleAllEvents.setOnAction(e -> {
            boolean all = toggleAllEvents.isSelected();
            comboEvents.setDisable(all);
            toggleAllEvents.setText(all ? "ON" : "OFF");
        });
        loadEvents();
    }

    private void loadEvents() {
        if (eventLogic == null) {
            eventLogic = new EventLogic(); // fallback (safe for now)
        }

        try {
            List<Event> events = eventLogic.getAllEvents();
            comboEvents.getItems().setAll(events);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load events");
        }
    }

    private void handleDiscountTypeChange() {
        DiscountType type = comboDiscountType.getValue();

        if (type == DiscountType.FREE) {
            txtDiscountValue.setDisable(true);
            txtDiscountValue.setText("0");
        } else {
            txtDiscountValue.setDisable(false);
        }
    }

    @FXML
    private void onCreateVoucher(ActionEvent event) {

        try {
            String name = txtName.getText();
            String desc = txtDescription.getText();
            DiscountType type = comboDiscountType.getValue();

            if (type == null) {
                showAlert("Select discount type");
                return;
            }

            double value = 0;

            if (type != DiscountType.FREE) {
                if (txtDiscountValue.getText().isBlank()) {
                    showAlert("Enter discount value");
                    return;
                }

                value = Double.parseDouble(txtDiscountValue.getText());
            }

            int eventId = 0;

            if (!toggleAllEvents.isSelected()) {

                Event selectedEvent = comboEvents.getValue();

                if (selectedEvent == null) {
                    showAlert("Select an event or enable 'All Events'");
                    return;
                }

                eventId = selectedEvent.getId();
            }

            voucherLogic.createVoucher(name, desc, value, type, eventId);

            showAlert("Voucher created successfully!");

        } catch (NumberFormatException e) {
            showAlert("Discount value must be a number");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
