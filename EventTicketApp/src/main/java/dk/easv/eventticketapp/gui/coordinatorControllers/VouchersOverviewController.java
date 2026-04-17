package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.bll.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VouchersOverviewController implements ApplicationServicesAware {

    private CoordinatorMainController coordinatorMainController;
    private final VoucherLogic voucherLogic = new VoucherLogic();

    @FXML private TableView<Voucher> voucherTable;
    @FXML private TableColumn<Voucher, String> voucherColumn;
    @FXML private TableColumn<Voucher, String> voucherTypeColumn;
    @FXML private TableColumn<Voucher, String> discountValueColumn;
    @FXML private TableColumn<Voucher, String> eventColumn;
    @FXML private TableColumn<Voucher, String> createdColumn;
    @FXML private TableColumn<Voucher, String> statusColumn;

    private Map<Integer, String> eventNameMap;


    public void setMainCoordinatorController(CoordinatorMainController coordinatorMainController) {this.coordinatorMainController = coordinatorMainController;}
private ApplicationServices services;

@Override
public void setApplicationServices(ApplicationServices services) {
    this.services = services;
}

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
                                ? cellData.getValue().getVoucherType().getDiscountType().toString()
                                : ""
                )
        );

        discountValueColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getVoucherType() != null
                                ? String.format(java.util.Locale.US, "%.2f",
                                cellData.getValue().getVoucherType().getDiscountValue())
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

            List<Event> events = services.getEventCoordinatorLogic().getEventsForUser(currentUser.getId());


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

        FXMLLoader loader = new ViewFactory(services).createLoader("gui/coordinatorViews/AddVoucher.fxml");

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        AddVoucherController controller = loader.getController();
        User user = SessionManager.getCurrentUser();

        if (voucher == null) {
            controller.init(user);
        } else {
            controller.initEdit(user, voucher);
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        loadVoucherData();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    @FXML
    private void onSeeVoucher() {
        Voucher selectedVoucher = voucherTable.getSelectionModel().getSelectedItem();

        if (selectedVoucher == null) {
            showAlert("Please select a voucher first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/VoucherTicket.fxml")
            );

            VBox view = loader.load();

            VoucherTicketController controller = loader.getController();
            controller.setVoucher(selectedVoucher);

            Stage stage = new Stage();
            stage.setTitle("Voucher");
            stage.setScene(new Scene(view));
            stage.initOwner(voucherTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Could not open voucher.");
        }
    }
}
