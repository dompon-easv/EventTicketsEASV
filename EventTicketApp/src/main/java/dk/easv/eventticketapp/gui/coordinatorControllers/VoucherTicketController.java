package dk.easv.eventticketapp.gui.coordinatorControllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.print.PrinterJob;

public class VoucherTicketController {
    @FXML private VBox ticketPaper;

    public void handlePrintTicket(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(ticketPaper.getScene().getWindow())) {
            boolean success = job.printPage(ticketPaper);
            if (success) {
                job.endJob();
            }
        }
    }
}
