package dk.easv.eventticketapp.gui.coordinatorControllers;

import dk.easv.eventticketapp.be.Voucher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VoucherTicketController {

    @FXML private VBox ticketPaper;

    @FXML private Label lblVoucherName;
    @FXML private Label lblVoucherType;
    @FXML private Label lblDiscount;
    @FXML private Label lblEvents;

    @FXML private ImageView imgQRCode;
    @FXML private ImageView imgBarcode;

    private Voucher currentVoucher;

    public void setVoucher(Voucher voucher) {
        this.currentVoucher = voucher;

        if (voucher == null) return;

        lblVoucherName.setText(voucher.getVoucherName());

        lblVoucherType.setText(
                voucher.getVoucherType() != null
                        ? voucher.getVoucherType().getDiscountType().toString()
                        : "N/A"
        );

        lblDiscount.setText(
                voucher.getVoucherType() != null
                        ? voucher.getVoucherType().getDiscountValue() + "%"
                        : "N/A"
        );

        lblEvents.setText(
                voucher.getEventName() != null
                        ? voucher.getEventName()
                        : "All Events"
        );

        String code = "V-" + voucher.getId();
        imgBarcode.setImage(generateBarcode(code));
        imgQRCode.setImage(generateQRCode(code));
    }

    private Image generateBarcode(String text) {
        try {
            Code128Writer writer = new Code128Writer();
            BitMatrix matrix = writer.encode(text, BarcodeFormat.CODE_128, 300, 80);

            WritableImage image = new WritableImage(300, 80);
            PixelWriter pw = image.getPixelWriter();

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 80; y++) {
                    pw.setArgb(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
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

            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);

            WritableImage image = new WritableImage(200, 200);
            PixelWriter pw = image.getPixelWriter();

            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    pw.setArgb(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void onPrintTicket(ActionEvent event) {
        Platform.runLater(() -> {
            PrinterJob job = PrinterJob.createPrinterJob();

            if (job != null) {
                Window owner = ticketPaper.getScene().getWindow();

                if (job.showPrintDialog(owner)) {
                    boolean success = job.printPage(ticketPaper);
                    if (success) {
                        job.endJob();
                    }
                }
            }
        });
    }

    @FXML
    public void onSendViaEmail(ActionEvent event) {

        if (currentVoucher == null) {
            showAlert("No voucher selected");
            return;
        }

        try {
            String subject = "Your Voucher - " + currentVoucher.getVoucherName();

            String body =
                    "Voucher: " + currentVoucher.getVoucherName() + "\n" +
                            "Type: " +
                            (currentVoucher.getVoucherType() != null
                                    ? currentVoucher.getVoucherType().getDiscountType()
                                    : "N/A") + "\n" +
                            "Discount: " +
                            (currentVoucher.getVoucherType() != null
                                    ? currentVoucher.getVoucherType().getDiscountValue() + "%"
                                    : "N/A") + "\n" +
                            "Event: " + currentVoucher.getEventName();

            String mailto = "mailto:" +
                    "?subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8) +
                    "&body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);

            Desktop.getDesktop().mail(new URI(mailto));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Could not open email client.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}