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

public class AddEditEventController {

    private final EventLogic eventLogic = new EventLogic();

    private boolean isEditMode = false;
    private Event currentEvent = null;

    @FXML private TextField nameField;
    @FXML private TextField locationField;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;

    @FXML private TextArea locationDescriptionField;
    @FXML private TextArea notesField;
    @FXML private VBox coordinatorContainer;
    @FXML private Label selectedCountLabel;

    @FXML private Label formTitle;
    @FXML private Button saveButton;

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
    public void onSaveEvent(ActionEvent actionEvent) {
        try {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String description = notesField.getText().trim();
            String locationDescription = locationDescriptionField.getText().trim();

            LocalDateTime start = combineDateTime(startDatePicker, startTimeCombo);
            LocalDateTime end = null;

            if (endDatePicker.getValue() != null && endTimeCombo.getValue() != null) {
                end = combineDateTime(endDatePicker, endTimeCombo);

                if (!end.isAfter(start)) {
                    throw new Exception("End time must be after start time");
                }
            }

            if (isEditMode) {

                currentEvent.setName(name);
                currentEvent.setLocation(location);
                currentEvent.setStartDate(start);
                currentEvent.setEndDate(end);
                currentEvent.setDescription(description);
                currentEvent.setLocationDescription(locationDescription);

                eventLogic.updateEvent(currentEvent);

                // 🔥 NEW — update coordinators
                List<Integer> selectedUsers = getSelectedCoordinatorIds();
                new EventCoordinatorLogic().updateCoordinators(currentEvent.getId(), selectedUsers);
            } else {
                // ➕ CREATE
                Event newEvent = new Event(
                        name,
                        location,
                        start,
                        end,
                        description,
                        locationDescription
                );

                Event createdEvent = eventLogic.createEvent(newEvent);

                List<Integer> selectedUsers = getSelectedCoordinatorIds();
                new EventCoordinatorLogic().assignCoordinators(createdEvent.getId(), selectedUsers);
            }

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
                    .filter(u -> u.getRole() == UserRole.COORDINATOR)
                    .toList();

            coordinatorContainer.getChildren().clear();

            for (User user : coordinators) {
                CheckBox cb = new CheckBox(user.getName() + " " + user.getSurname());
                cb.setUserData(user.getId());
                cb.getStyleClass().add("modern-checkbox");

                cb.selectedProperty().addListener((obs, oldVal, newVal) -> updateSelectedLabel());

                coordinatorContainer.getChildren().add(cb);
            }

            if (isEditMode && currentEvent != null) {
                preselectCoordinators(currentEvent.getId());
            }

            updateSelectedLabel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateSelectedLabel() {
        List<String> selectedNames = new ArrayList<>();

        for (Node node : coordinatorContainer.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                selectedNames.add(cb.getText());
            }
        }

        int count = selectedNames.size();

        if (count == 0) {
            selectedCountLabel.setText("No coordinators selected");
        } else if (count == 1) {
            selectedCountLabel.setText("1 coordinator: " + selectedNames.get(0));
        } else {
            selectedCountLabel.setText(count + " coordinators: " + String.join(", ", selectedNames));
        }
    }
    public void setEditMode(Event event) {
        this.isEditMode = true;
        this.currentEvent = event;

        // 🔥 Change UI text
        formTitle.setText("Edit Event");
        saveButton.setText("Save Changes");

        // 🔥 Pre-fill fields
        nameField.setText(event.getName());
        locationField.setText(event.getLocation());
        notesField.setText(event.getDescription());
        locationDescriptionField.setText(event.getLocationDescription());

        startDatePicker.setValue(event.getStartDate().toLocalDate());
        startTimeCombo.setValue(event.getStartDate().toLocalTime().toString());

        if (event.getEndDate() != null) {
            endDatePicker.setValue(event.getEndDate().toLocalDate());
            endTimeCombo.setValue(event.getEndDate().toLocalTime().toString());
        }
    }
    private void preselectCoordinators(int eventId) {
        try {
            EventCoordinatorLogic ecLogic = new EventCoordinatorLogic();
            List<Integer> assignedIds = ecLogic.getCoordinatorIdsForEvent(eventId);

            for (Node node : coordinatorContainer.getChildren()) {
                if (node instanceof CheckBox cb) {
                    Integer userId = (Integer) cb.getUserData();

                    if (assignedIds.contains(userId)) {
                        cb.setSelected(true);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}