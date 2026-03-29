package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.bll.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VouchersOverviewController {

    private EventCoordinatorLogic eventCoordinatorLogic;
    private SessionManager sessionManager;
    private EventLogic eventLogic;
    private TicketTypeManager ticketTypeManager;
    private UserManager userManager;
    private CoordinatorMainController coordinatorMainController;

    public void showEvents(ActionEvent actionEvent) {
       coordinatorMainController.loadView("CoordinatorHome.fxml");
    }

    public void onCreateVoucher(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/dk/easv/eventticketapp/gui/coordinatorViews/AddVoucher.fxml"));
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
