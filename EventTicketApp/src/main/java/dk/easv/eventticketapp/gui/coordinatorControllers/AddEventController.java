package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class AddEventController {

    private final EventLogic eventLogic = new EventLogic();

    @FXML private TextField nameField;
    @FXML private TextField locationField;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;

    @FXML private TextArea locationDescriptionField;
    @FXML private TextArea notesField;

    @FXML
    public void initialize() {
        loadTimeOptions(startTimeCombo);
        loadTimeOptions(endTimeCombo);
        startDatePicker.setValue(java.time.LocalDate.now());
        startTimeCombo.setValue("12:00");
        endTimeCombo.setValue("13:00");

    }

    @FXML
    public void onCreateEvent(ActionEvent actionEvent) {
        try {
            String name = nameField.getText() != null ? nameField.getText().trim() : "";
            String location = locationField.getText() != null ? locationField.getText().trim() : "";
            String description = notesField.getText() != null ? notesField.getText().trim() : "";
            String locationDescription = locationDescriptionField.getText() != null ? locationDescriptionField.getText().trim() : "";

            LocalDateTime start = combineDateTime(startDatePicker, startTimeCombo);

            LocalDateTime end = null;
            if (endDatePicker.getValue() != null || endTimeCombo.getValue() != null) {
                end = combineDateTime(endDatePicker, endTimeCombo);
            }

            Event event = new Event(
                    name,
                    location,
                    start,
                    end,
                    description,
                    locationDescription
            );

            eventLogic.createEvent(event);
            closeBtn(actionEvent);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadTimeOptions(ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        for (int hour = 0; hour < 24; hour++) {
            for (int min = 0; min < 60; min += 15) {
                comboBox.getItems().add(String.format("%02d:%02d", hour, min));
            }
        }
    }

    private LocalDateTime combineDateTime(DatePicker datePicker, ComboBox<String> timeCombo) throws Exception {
        if (datePicker.getValue() == null) {
            throw new Exception("Date is required");
        }

        String timeText = timeCombo.getValue();
        if (timeText == null || timeText.isBlank()) {
            throw new Exception("Time must be selected");
        }

        LocalTime time = LocalTime.parse(timeText);
        return LocalDateTime.of(datePicker.getValue(), time);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not create event");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void closeBtn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(
                            "/dk/easv/eventticketapp/gui/coordinatorViews/CoordinatorHome.fxml"
                    ))
            );

            Node node = loader.load();
            CoordinatorMainController.staticContentArea.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}