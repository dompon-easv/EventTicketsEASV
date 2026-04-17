package dk.easv.eventticketapp;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ViewFactory;
import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.dao.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {

        ApplicationServices services = new ApplicationServices();

        ViewFactory viewFactory = new ViewFactory(services);

        FXMLLoader loader = viewFactory.createLoader("gui/Login.fxml");
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}