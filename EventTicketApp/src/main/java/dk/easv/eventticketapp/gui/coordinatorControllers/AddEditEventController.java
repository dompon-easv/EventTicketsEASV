package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.enums.UserRole;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.dao.UserDAO;
import dk.easv.eventticketapp.gui.adminControllers.AdminMainController;
import dk.easv.eventticketapp.gui.adminControllers.EventsController;
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

public class AddEditEventController implements ApplicationServicesAware {



    private boolean isEditMode = false;
    private Event currentEvent;
    private CoordinatorMainController coordinatorMainController;

    @FXML private TextField nameField;
    @FXML private TextField maxTicketsField;
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
    @FXML private VBox eventDetailsSection;

    private List<User> coordinators = new ArrayList<>();

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    @FXML
    public void initialize() {
        setupTimeInputs();
        loadCoordinators();
    }

    public void init()
    {
         if(SessionManager.getCurrentUser().getRole() == UserRole.ADMIN)
        {
            eventDetailsSection.setVisible(false);
            eventDetailsSection.setManaged(false);
        }
    }

    public void setCoordinatorMainController(CoordinatorMainController coordinatorMainController) {
        this.coordinatorMainController = coordinatorMainController;
    }

    private void setupTimeInputs() {
        loadTimeOptions(startTimeCombo);
        loadTimeOptions(endTimeCombo);
        startDatePicker.setValue(LocalDate.now());
        startTimeCombo.setValue("12:00");
        endTimeCombo.setValue("13:00");
    }

    public void populateEvent(Event event) {
        if (event == null) return;

        this.currentEvent = event;
        this.isEditMode = true;

        updateFormMode();
        fillForm(event);
        preselectCoordinators(event.getId());
        updateSelectedLabel();
    }

    private void updateFormMode() {
        formTitle.setText(isEditMode ? "Edit Event" : "Create New Event");
        saveButton.setText(isEditMode ? "Save Changes" : "Create Event");
    }

    private void fillForm(Event event) {
        nameField.setText(event.getName());
        maxTicketsField.setText(String.valueOf(event.getMaxTickets()));
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

    @FXML
    public void onSaveEvent(ActionEvent actionEvent) {
        try {
            Event event = buildEventFromInput();

            if (isEditMode) {
                updateEvent(event);
            } else {
                createEvent(event);
            }

            closeBtn(actionEvent);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private Event buildEventFromInput() throws Exception {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String description = notesField.getText().trim();
        String locationDescription = locationDescriptionField.getText().trim();
        int maxTickets = parseMaxTickets();

        if (isEditMode && currentEvent != null) {
            if (services.getTicketTypeManager() != null) {
                int currentTotalTickets = services.getTicketTypeManager().getTotalTicketQuantityForEvent(currentEvent.getId());
                if (maxTickets < currentTotalTickets) {
                    throw new Exception(String.format(
                            "Cannot reduce event capacity to %d tickets!\n\n" +
                                    "Current ticket types total: %d tickets\n" +
                                    "You have two options:\n" +
                                    "1. Increase the capacity to at least %d tickets, OR\n" +
                                    "2. Delete or reduce some ticket types first.\n\n" +
                                    "Current total from ticket types: %d tickets",
                            maxTickets, currentTotalTickets, currentTotalTickets, currentTotalTickets
                    ));
                }
            }
        }

        LocalDateTime start = combineDateTime(startDatePicker, startTimeCombo);
        LocalDateTime end = null;

        if (endDatePicker.getValue() != null && endTimeCombo.getValue() != null) { // validation should be moved to logic
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
            currentEvent.setMaxTickets(maxTickets);
            return currentEvent;
        }

        return new Event(name, location, start, end, description, locationDescription, maxTickets);
    }

    private void createEvent(Event event) throws Exception {
        Event createdEvent = services.getEventLogic().createEvent(event);
        List<Integer> selectedUsers = getSelectedCoordinatorIds();
        services.getEventCoordinatorLogic().assignCoordinators(createdEvent.getId(), selectedUsers);

        if (services.getTicketTypeManager() != null) {
            services.getTicketTypeManager().setCurrentEvent(createdEvent);
        }
    }

    private void updateEvent(Event event) throws Exception {
        services.getEventLogic().updateEvent(event);
        List<Integer> selectedUsers = getSelectedCoordinatorIds();
        services.getEventCoordinatorLogic().updateCoordinators(event.getId(), selectedUsers);

        if (services.getTicketTypeManager() != null) {
            services.getTicketTypeManager().setCurrentEvent(event);
        }
    }

    private int parseMaxTickets() throws Exception {
        String text = maxTicketsField.getText().trim();
        if (text.isEmpty()) {
            throw new Exception("Max tickets is required");
        }

        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new Exception("Max tickets must be a valid number");
        }

        if (value <= 0) {
            throw new Exception("Max tickets must be greater than 0");
        }

        return value;
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

    private void loadCoordinators() {
        try {
            coordinators = services.getUserManager().getAllUsers().stream()
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

    private void preselectCoordinators(int eventId) {
        try {
            List<Integer> assignedIds = services.getEventCoordinatorLogic().getCoordinatorIdsForEvent(eventId);

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

    private void loadTimeOptions(ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        for (int hour = 0; hour < 24; hour++) {
            for (int min = 0; min < 60; min += 15) {
                comboBox.getItems().add(String.format("%02d:%02d", hour, min));
            }
        }
    }

    private LocalDateTime combineDateTime(DatePicker datePicker, ComboBox<String> timeCombo) throws Exception {
        if (datePicker.getValue() == null) throw new Exception("Date is required");

        String timeText = timeCombo.getValue();
        if (timeText == null || timeText.isBlank()) throw new Exception("Time must be selected");

        LocalTime time = LocalTime.parse(timeText);
        return LocalDateTime.of(datePicker.getValue(), time);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void closeBtn(ActionEvent actionEvent) {
        if (SessionManager.getCurrentUser().getRole() == UserRole.COORDINATOR) {
            coordinatorMainController.loadView("CoordinatorHome.fxml");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(
                        Objects.requireNonNull(getClass().getResource(
                                "/dk/easv/eventticketapp/gui/adminViews/Events.fxml"
                        ))
                );

                Node node = loader.load();
                AdminMainController.staticContentArea.getChildren().setAll(node);
                Object controller = loader.getController();
                if (controller instanceof EventsController eventsController) {
                    eventsController.init();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }}