package dk.easv.eventticketapp.gui;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;
import dk.easv.eventticketapp.bll.AuthenticationLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.SessionManager;
import dk.easv.eventticketapp.bll.UserManager;
import dk.easv.eventticketapp.gui.adminControllers.AdminMainController;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;

    private AuthenticationLogic authenticationLogic;
    private SessionManager sessionManager;
    private UserManager userManager;
    private EventLogic eventLogic;

    public void setAuthenticationLogic (AuthenticationLogic authenticationLogic) {
        this.authenticationLogic = authenticationLogic;
    }
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    public void setEventLogic(EventLogic eventLogic) {
        this.eventLogic = eventLogic;
    }


    @FXML private  void onLoginAction(ActionEvent actionEvent) throws IOException {

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        try{
            User user = authenticationLogic.login(username,password);
            System.out.println(user);
            SessionManager.setCurrentUser(user);

            if(user!=null){
                //System.out.println("wrong username or password");
                //return;

            loadMainView(actionEvent, user.getRole()); }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    private void loadMainView(ActionEvent actionEvent,UserRole role) throws IOException {

        String fxmlPath = switch (role){
            case ADMIN -> "/dk/easv/eventticketapp/gui/adminViews/AdminMain.fxml";
            case COORDINATOR -> "/dk/easv/eventticketapp/gui/coordinatorViews/CoordinatorMain.fxml";
    };
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load());

        if(role == UserRole.ADMIN){
            AdminMainController adminMainController = fxmlLoader.getController();
            adminMainController.setAuthenticationLogic(authenticationLogic);
            adminMainController.setUserManager(userManager);
            adminMainController.setEventLogic(eventLogic);
        }
        if(role == UserRole.COORDINATOR){
            CoordinatorMainController coordinatorMainController = fxmlLoader.getController();
            coordinatorMainController.setAuthenticationLogic(authenticationLogic);
            coordinatorMainController.setUserManager(userManager);
        }
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();

}


}
