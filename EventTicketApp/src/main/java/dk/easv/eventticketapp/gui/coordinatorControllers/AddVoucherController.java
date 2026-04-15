package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.bll.VoucherLogic;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class AddVoucherController {

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<DiscountType> comboDiscountType;
    @FXML private TextField txtDiscountValue;
    @FXML private ToggleButton toggleAllEvents;
    @FXML private ComboBox<Event> comboEvents;
    @FXML private Button btnCreate;

    private EventLogic eventLogic;
    private VoucherLogic voucherLogic = new VoucherLogic();
    private EventCoordinatorLogic eventCoordinatorLogic;
    private User currentUser;
    private Voucher voucherToEdit;
    private boolean isEditMode = false;

    public void setEvents(List<Event> events) {
        comboEvents.getItems().setAll(events);
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }

    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        this.eventCoordinatorLogic = eventCoordinatorLogic;
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
    }

    public void init(User user, EventCoordinatorLogic eventCoordinatorLogic) {
        this.currentUser = user;
        this.eventCoordinatorLogic = eventCoordinatorLogic;
        
        loadEvents();
    }

    public void initEdit(User user, EventCoordinatorLogic logic, Voucher voucher) {
        this.currentUser = user;
        this.eventCoordinatorLogic = logic;
        this.voucherToEdit = voucher;
        this.isEditMode = true;

        loadEvents();
        populateFields();

        btnCreate.setText("Update Voucher");
    }

    private void loadEvents() {
        try {
            List<Event> events =
                    eventCoordinatorLogic.getEventsForUser(currentUser.getId());

            comboEvents.getItems().setAll(events);

        } catch (Exception e) {
            e.printStackTrace();
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

    private void populateFields() {
        txtName.setText(voucherToEdit.getVoucherType().getName());
        txtDescription.setText(voucherToEdit.getVoucherType().getDescription());
        comboDiscountType.setValue(voucherToEdit.getVoucherType().getDiscountType());
        txtDiscountValue.setText(
                String.valueOf(voucherToEdit.getVoucherType().getDiscountValue())
        );

        if (voucherToEdit.getEventId() == 0) {
            toggleAllEvents.setSelected(true);
            comboEvents.setDisable(true);
        } else {
            comboEvents.getItems().stream()
                    .filter(e -> e.getId() == voucherToEdit.getEventId())
                    .findFirst()
                    .ifPresent(comboEvents::setValue);
        }
    }

    @FXML
    private void onCreateVoucher(ActionEvent event) {

        try {
            String name = txtName.getText();
            String desc = txtDescription.getText();
            DiscountType type = comboDiscountType.getValue();

            if (name == null || name.isBlank()) {
                showAlert("Name is required");
                return;
            }

            if (desc == null || desc.isBlank()) {
                showAlert("Description is required");
                return;
            }

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

            if (isEditMode) {

                voucherToEdit.getVoucherType().setName(name);
                voucherToEdit.getVoucherType().setDescription(desc);
                voucherToEdit.getVoucherType().setDiscountType(type);
                voucherToEdit.getVoucherType().setDiscountValue(value);
                voucherToEdit.setEventId(eventId);

                voucherLogic.updateVoucher(voucherToEdit);

                showAlert("Voucher updated!");
            } else {
                voucherLogic.createVoucher(name, desc, value, type, eventId);
                showAlert("Voucher created!");
            }

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }

    private void closeWindow() {
        ((Stage) txtName.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public void onCloseWindow(ActionEvent actionEvent) {
        closeWindow();
    }
}
