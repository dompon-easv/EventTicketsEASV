package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.bll.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VouchersOverviewController {

    private EventCoordinatorLogic eventCoordinatorLogic;
    private SessionManager sessionManager;
    private EventLogic eventLogic;
    private TicketTypeManager ticketTypeManager;
    private UserManager userManager;
    private CoordinatorMainController coordinatorMainController;
    private final VoucherLogic voucherLogic = new VoucherLogic();
    User currentUser = SessionManager.getCurrentUser();

    @FXML private TableView<Voucher> voucherTable;
    @FXML private TableColumn<Voucher, String> voucherColumn;
    @FXML private TableColumn<Voucher, String> voucherTypeColumn;
    @FXML private TableColumn<Voucher, String> eventColumn;
    @FXML private TableColumn<Voucher, String> createdColumn;
    @FXML private TableColumn<Voucher, String> statusColumn;

    private Map<Integer, String> eventNameMap;

    public void initData() {
        loadEvents();
        loadVoucherData();
    }

    public void initialize() {

        voucherColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getVoucherName()));

        voucherTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getVoucherType() != null
                                ? cellData.getValue().getVoucherType().getDiscountType().name()
                                : ""
                )
        );

        eventColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getEventName();
            return new SimpleStringProperty(
                    name == null ? "All Events" : name
            );
        });

        createdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCreatedDate() != null
                                ? cellData.getValue().getCreatedDate().toString()
                                : ""
                )
        );

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStatus() != null
                                ? cellData.getValue().getStatus().name()
                                : ""
                )
        );
    }

    private void loadEvents() {
        try {
            User currentUser = SessionManager.getCurrentUser();

            List<Event> events = eventCoordinatorLogic.getEventsForUser(currentUser.getId());

            eventNameMap = events.stream()
                    .collect(Collectors.toMap(Event::getId, Event::getName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVoucherData() {
        try {
            User currentUser = SessionManager.getCurrentUser();

            voucherTable.setItems(
                    FXCollections.observableArrayList(
                            voucherLogic.getVouchersForCoordinator(currentUser.getId())
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEvents(ActionEvent actionEvent) {
        coordinatorMainController.loadView("CoordinatorHome.fxml");
    }

    public void onCreateVoucher(ActionEvent e) throws IOException {
        openVoucherWindow(null);
    }

    public void onEditVoucher(ActionEvent e) throws IOException {
        Voucher selected = voucherTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select a voucher to edit.");
            return;
        }

        openVoucherWindow(selected);
    }

    public void onDeleteVoucher(ActionEvent e) {

        Voucher selected = voucherTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select a voucher to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete voucher?");
        confirm.setContentText("This cannot be undone.");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    voucherLogic.deleteVoucher(selected.getId());
                    loadVoucherData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Delete failed.");
                }
            }
        });
    }

    private void openVoucherWindow(Voucher voucher) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/AddVoucher.fxml")
        );

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        AddVoucherController controller = loader.getController();

        User user = SessionManager.getCurrentUser();

        if (voucher == null) {
            controller.init(user, eventCoordinatorLogic);
        } else {
            controller.initEdit(user, eventCoordinatorLogic, voucher);
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();

        loadVoucherData();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }


    public void setEventCoordinatorLogic(EventCoordinatorLogic eventCoordinatorLogic) {
        this.eventCoordinatorLogic = eventCoordinatorLogic;
    }

    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }

    public void setTicketTypeManager(TicketTypeManager ticketTypeManager) {
        this.ticketTypeManager = ticketTypeManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setMainCoordinatorController(CoordinatorMainController coordinatorMainController) {
        this.coordinatorMainController = coordinatorMainController;
    }
}
