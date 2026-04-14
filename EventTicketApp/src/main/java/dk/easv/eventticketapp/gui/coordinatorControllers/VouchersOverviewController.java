package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.bll.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class VouchersOverviewController {



    private EventCoordinatorLogic eventCoordinatorLogic;
    private SessionManager sessionManager;
    private EventLogic eventLogic;
    private TicketTypeManager ticketTypeManager;
    private UserManager userManager;
    private CoordinatorMainController coordinatorMainController;
    private VoucherLogic voucherLogic = new VoucherLogic();

    @FXML private TableView<Voucher> voucherTable;
    @FXML private TableColumn<Voucher, String> voucherColumn;
    @FXML private TableColumn<Voucher, String> eventColumn;
    @FXML private TableColumn<Voucher, String> createdColumn;
    @FXML private TableColumn<Voucher, String> statusColumn;



    public void showEvents(ActionEvent actionEvent) {
       coordinatorMainController.loadView("CoordinatorHome.fxml");
    }

    public void initialize() {
        // Map the BE properties to the columns
        voucherColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getVoucherName()));

        eventColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEventId() == 0 ? "All Events" : "ID: " + cellData.getValue().getEventId()));

        createdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedDate().toString()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().name()));

        loadVoucherData();
    }

    private void loadVoucherData() {
        try {
            voucherTable.setItems(FXCollections.observableArrayList(voucherLogic.getAllVouchers()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreateVoucher(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/AddVoucher.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
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
