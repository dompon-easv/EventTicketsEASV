package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class CoordinatorHomeController {
    public void createEventBtn(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/coordinatorViews/eventManagement/AddEditEvent.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Add Event");
        stage.setScene(scene);
        stage.show();
    }

    public void openEvent(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/coordinatorViews/eventManagement/EventHeader.fxml"));
        Node node = fxmlLoader.load();
        //Injection into contentArea without opening new window
        CoordinatorMainController.staticContentArea.getChildren().setAll(node);
    }

    public void showVouchers(ActionEvent actionEvent) {
    }

    public void showEvents(ActionEvent actionEvent) throws IOException {
    }
}
