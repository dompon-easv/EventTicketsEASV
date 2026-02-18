package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class EventHeaderController {
  @FXML private Button btnOverview;
  @FXML private Button btnTicketTypes;
  @FXML private Button btnIssueTickets;
  @FXML private Button btnIssuedTickets;
  @FXML private StackPane contentArea;

    public void handleBack(ActionEvent actionEvent) {
    }

    public void handleTabChange(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();

        contentArea.getChildren().clear();

        try {
            String fxmlFile = "";

            if (clickedButton == btnOverview) {
                fxmlFile = "CoordinatorEventOverview.fxml";
            } else if (clickedButton == btnTicketTypes) {
                fxmlFile = "TicketTypes.fxml";
            } else if (clickedButton == btnIssueTickets) {
                fxmlFile = "IssueTicket.fxml";
            } else if (clickedButton == btnIssuedTickets) {
                fxmlFile = "IssuedTickets.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/eventticketapp/gui/coordinatorViews/eventManagement/" + fxmlFile));
            Node node = loader.load();


            contentArea.getChildren().add(node);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
