package dk.easv.eventticketapp.gui.adminControllers;


import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserCardController {

    @FXML private Label lblName;
    @FXML private Label lblUsername;
    @FXML private Label lblEmail;
    @FXML private Label lblRole;

    private User user;

    public void setUser(User user) {
        this.user = user;

        if (user == null) return;

        lblName.setText(user.getName() + " " + user.getSurname());
        lblUsername.setText("@" + user.getUsername());
        lblEmail.setText(user.getEmail());
        lblRole.setText(user.getRole().toString());

        applyRoleStyle(user.getRole());
    }

    private void applyRoleStyle(UserRole role) {
        lblRole.getStyleClass().removeAll(
                "role-admin",
                "role-coordinator",
                "role-user"
        );

        switch (role) {
            case ADMIN -> lblRole.getStyleClass().add("role-admin");
            case COORDINATOR -> lblRole.getStyleClass().add("role-coordinator");
            default -> lblRole.getStyleClass().add("role-user");
        }
    }
}