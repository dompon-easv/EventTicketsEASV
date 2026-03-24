package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.UserManager;
import dk.easv.eventticketapp.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    @FXML private VBox coordinatorContainer;

    @FXML
    public void initialize() {
        loadTimeOptions(startTimeCombo);
        loadTimeOptions(endTimeCombo);
        startDatePicker.setValue(LocalDate.now());
        startTimeCombo.setValue("12:00");
        endTimeCombo.setValue("13:00");
        loadCoordinators();

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
            if (endDatePicker.getValue() != null && endTimeCombo.getValue() != null) {
                end = combineDateTime(endDatePicker, endTimeCombo);

                if (!end.isAfter(start)) {
                    throw new Exception("End time must be after start time");
                }
            }

            // ✅ CREATE EVENT OBJECT HERE (ONLY HERE)
            Event event = new Event(
                    name,
                    location,
                    start,
                    end,
                    description,
                    locationDescription
            );

            // ✅ SAVE EVENT
            Event createdEvent = eventLogic.createEvent(event);

            // ✅ ASSIGN COORDINATORS
            List<Integer> selectedUsers = getSelectedCoordinatorIds();

            EventCoordinatorLogic ecLogic = new EventCoordinatorLogic();
            ecLogic.assignCoordinators(createdEvent.getId(), selectedUsers);

            closeBtn(actionEvent);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private List<Integer> getSelectedCoordinatorIds() {
        List<Integer> selected = new ArrayList<>();

        for (Node node : coordinatorContainer.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected() && cb.getUserData() != null) {
                selected.add((Integer) cb.getUserData());
            }
        }

        return selected;
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
    private List<User> coordinators = new ArrayList<>();
    

    private void loadCoordinators() {
        try {
            UserManager userManager = new UserManager(new UserDAO());

            coordinators = userManager.getAllUsers().stream()
                    .filter(u -> u.getRole() == UserRole.COORDINATOR) // ✅ FIXED
                    .toList();

            coordinatorContainer.getChildren().clear();

            for (User user : coordinators) {
                CheckBox cb = new CheckBox(user.getName() + " " + user.getSurname());
                cb.setUserData(user.getId()); // ✅ NOW WORKS
                cb.getStyleClass().add("modern-checkbox");

                coordinatorContainer.getChildren().add(cb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}