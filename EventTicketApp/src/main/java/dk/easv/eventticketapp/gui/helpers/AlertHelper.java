package dk.easv.eventticketapp.gui.helpers;

import javafx.scene.control.Alert;

public  class AlertHelper {

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);

        applyCss(alert);

        alert.showAndWait();
    }
    private static void applyCss(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                AlertHelper.class
                        .getResource("gui/style.css")
                        .toExternalForm()
        );
    }

}

