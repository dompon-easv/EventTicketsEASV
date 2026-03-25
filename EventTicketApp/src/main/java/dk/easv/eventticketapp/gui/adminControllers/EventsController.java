package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EventsController {
    @FXML Label lblEventNumber;
    @FXML
    Label lblCoordinatorsNumber;

    private UserManager userManager;
    private EventLogic eventLogic;
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        lblCoordinatorsNumber.setText(String.valueOf(userManager.getCoordinatorCount()));
    }
    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
        lblEventNumber.setText(String.valueOf(eventLogic.getEventCount()));
    }


}
