package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;

import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.bll.EventCoordinatorLogic;
import dk.easv.eventticketapp.bll.EventLogic;
import dk.easv.eventticketapp.bll.TicketManager;
import dk.easv.eventticketapp.bll.TicketTypeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class EventCardController implements ApplicationServicesAware {

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label coordinatorLabel;
    @FXML private Label ticketsLabel;
    @FXML private VBox clickArea;
    @FXML private ProgressBar ticketsProgressBar;

    private Event event;
   ;
    private Runnable onDeleteSuccess;
    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }


    private Consumer<Event> onCardClick;

    public void setEvent(Event event) {
        this.event = event;

        titleLabel.setText(event.getName());
        locationLabel.setText("📍 " + event.getLocation());

        dateLabel.setText("📅 " + EventDateTimeFormatter.formatEventRange(event));

        try {
            if (services.getEventCoordinatorLogic() != null) {
                int count = services.getEventCoordinatorLogic()
                        .getCoordinatorIdsForEvent(event.getId())
                        .size();

                coordinatorLabel.setText("👤 " + count + " coordinator(s)");
            } else {
                coordinatorLabel.setText("👤 0 coordinator(s)");
            }
        } catch (Exception e) {
            coordinatorLabel.setText("👤 error");
            e.printStackTrace();
        }

        try {
            if (services.getTicketManager() != null) {

                int sold = services.getTicketManager().getTotalTicketsSoldForEvent(event.getId());
                int max = services.getTicketManager().getTotalMaxTicketsForEvent(event.getId());

                if (max > 0) {
                    double progress = (double) sold / max;

                    ticketsLabel.setText(
                            sold + " / " + max + " tickets (" + (int)(progress * 100) + "%)"
                    );

                    ticketsProgressBar.setProgress(progress);

                } else {
                    ticketsLabel.setText(sold + " tickets issued");
                    ticketsProgressBar.setProgress(0);
                }

            } else {
                ticketsLabel.setText("0 tickets issued");
                ticketsProgressBar.setProgress(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ticketsLabel.setText("Error loading tickets");
            ticketsProgressBar.setProgress(0);
        }

        if (clickArea != null) {
            clickArea.setOnMouseClicked(e -> {
                if (onCardClick != null) {
                    onCardClick.accept(event);
                }
            });
        }
    }

    public void setOnCardClick(Consumer<Event> onCardClick) {
        this.onCardClick = onCardClick;
    }


    public void setOnDeleteSuccess(Runnable onDeleteSuccess) {
        this.onDeleteSuccess = onDeleteSuccess;
    }

    public void handleDelete(ActionEvent actionEvent) {

        if (event == null) {
            System.out.println("no event");
            return;
        }

        try {
            services.getTicketTypeManager().deleteEvent(event.getId());
            services.getEventCoordinatorLogic().deleteEvent(event);

            if (onDeleteSuccess != null) {
                onDeleteSuccess.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}