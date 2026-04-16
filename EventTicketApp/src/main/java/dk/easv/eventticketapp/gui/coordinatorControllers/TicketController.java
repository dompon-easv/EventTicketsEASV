package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.IssuedTicket;
import dk.easv.eventticketapp.be.Event;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.print.PrinterJob;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.common.BitMatrix;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.stage.Window;

import java.net.URI;
import java.awt.Desktop;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class TicketController {

    @FXML private BorderPane rootPane;
    @FXML private HBox ticketContent;
    @FXML private Label lblEvent;
    @FXML private Label lblTicketType;
    @FXML private Label lblLocation;
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblQuantity;
    @FXML private Label lblPrice;
    @FXML private ImageView imgBarcode;
    @FXML private ImageView imgQRCode;

    private IssuedTicket currentTicket;

    public void setTicket(IssuedTicket ticket, Event event) {
        this.currentTicket = ticket;

        lblTicketType.setText(ticket.getTicketType());
        lblQuantity.setText(String.valueOf(ticket.getQuantity()));
        lblPrice.setText(String.format("kr. %.2f", ticket.getPrice()));

        lblEvent.setText(event.toString());
        lblLocation.setText(event.getLocation());
        lblDate.setText(event.getStartDate().toLocalDate().toString());
        lblTime.setText(event.getStartDate().toLocalTime().toString());

        if (ticket.getBarcode() != null) {
            String code = ticket.getBarcode();
            imgBarcode.setImage(generateBarcode(ticket.getBarcode()));
            imgQRCode.setImage(generateQRCode(code));
        }
    }

    private Image generateBarcode(String text) {
        try {
            Code128Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, 300, 80);

            WritableImage image = new WritableImage(300, 80);
            PixelWriter pixelWriter = image.getPixelWriter();

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 80; y++) {
                    boolean bit = bitMatrix.get(x, y);
                    pixelWriter.setArgb(x, y, bit ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Image generateQRCode(String text) {
        try {
            com.google.zxing.qrcode.QRCodeWriter writer =
                    new com.google.zxing.qrcode.QRCodeWriter();

            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);

            WritableImage image = new WritableImage(200, 200);
            PixelWriter pixelWriter = image.getPixelWriter();

            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    boolean bit = bitMatrix.get(x, y);
                    pixelWriter.setArgb(x, y, bit ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageView createPreview() {
        WritableImage image = ticketContent.snapshot(null, null);
        ImageView preview = new ImageView(image);

        preview.setPreserveRatio(true);
        preview.setFitWidth(500);

        return preview;
    }

    public void onPrintTicket(ActionEvent actionEvent) {


        ImageView preview = createPreview();

        Button printBtn = new Button("Confirm Print");
        printBtn.setOnAction(e -> printNode());

        VBox layout = new VBox(10, preview, printBtn);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Stage previewStage = new Stage();
        previewStage.setTitle("Print Preview");
        previewStage.setScene(new Scene(layout));
        previewStage.show();
    }

    private void printNode() {
        Platform.runLater(() -> {
            PrinterJob job = PrinterJob.createPrinterJob();

            if (job != null) {
                Window owner = rootPane.getScene().getWindow();
                boolean proceed = job.showPrintDialog(owner);
                if (proceed) {
                    boolean success = job.printPage(ticketContent);
                    if (success) {
                        job.endJob();
                    }
                }
            } else {
                System.out.println("Could not create print job");
            }
        });
    }

    public void onSendViaEmail(ActionEvent actionEvent) {

        if (currentTicket == null) {
            showAlert("No ticket selected");
            return;
        }

        try {
            String email = currentTicket.getEmail();

            String subject = "Your Event Ticket - " + lblEvent.getText();

            String body =
                    "Hello " + currentTicket.getCustomerName() + ",\n\n" +
                            "Here is your ticket:\n\n" +
                            "Event: " + lblEvent.getText() + "\n" +
                            "Type: " + lblTicketType.getText() + "\n" +
                            "Date: " + lblDate.getText() + "\n" +
                            "Time: " + lblTime.getText() + "\n" +
                            "Quantity: " + lblQuantity.getText() + "\n" +
                            "Price: " + lblPrice.getText() + "\n\n" +
                            "Thank you for your purchase!";

            String mailto = "mailto:" + email +
                    "?subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8) +
                    "&body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);

            URI uri = new URI(mailto);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().mail(uri);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Could not open email client.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
