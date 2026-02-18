package dk.easv.eventticketapp.gui;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.be.UserRole;
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
    
    @FXML private  void onLoginAction(ActionEvent actionEvent) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        UserRole role;

        if(username.equals("admin") && password.equals("admin")) {
            role = UserRole.ADMIN;
        } else if(username.equals("coordinator") && password.equals("coordinator")) {
            role = UserRole.COORDINATOR;
        } else {
            System.out.println("Invalid username or password");
            return;
        }

        //Switch scene based on the ENUM
        try{
            loadMainView(actionEvent, role);
        } catch (IOException e){
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
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
}}
