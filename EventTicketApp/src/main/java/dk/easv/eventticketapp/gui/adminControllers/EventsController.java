package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.bll.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EventsController {
    @FXML
    Label lblCoordinatorsNumber;

    private UserManager userManager;
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        lblCoordinatorsNumber.setText(String.valueOf(userManager.getCoordinatorCount()));

    }


}
