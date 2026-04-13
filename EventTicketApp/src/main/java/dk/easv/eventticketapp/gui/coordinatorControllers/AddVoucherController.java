package dk.easv.eventticketapp.gui.coordinatorControllers;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.bll.VoucherManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.event.ActionEvent;


public class AddVoucherController {

@FXML private TextField txtName, txtDiscountValue;
@FXML private TextArea txtDescription;
@FXML private ComboBox<DiscountType> comboDiscountType;
@FXML private ComboBox<Event> comboEvents;
@FXML private ToggleButton toggleAllEvents;

private VoucherManager voucherManager = new VoucherManager();

@FXML
public void initialize() {
    comboDiscountType.setItems(FXCollections.observableArrayList(DiscountType.values()));
    // Load events from your EventLogic here...
}

@FXML
private void handleCreateVoucher(ActionEvent event) {
    try {
        String name = txtName.getText();
        String desc = txtDescription.getText();
        double val = Double.parseDouble(txtDiscountValue.getText());
        DiscountType type = comboDiscountType.getValue();
        int eventId = toggleAllEvents.isSelected() ? 0 : comboEvents.getValue().getId();

        voucherManager.createSpecialVoucher(name, desc, val, type, eventId);

        // Close window and refresh table
    } catch (Exception e) {
        // Handle validation/SQL errors
    }
}}