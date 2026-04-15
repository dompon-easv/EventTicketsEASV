package dk.easv.eventticketapp.gui.coordinatorControllers.eventManagement;


import dk.easv.eventticketapp.be.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDateTimeFormatter {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    public static String formatEventRange(Event event) {

        if (event == null) return "No date";

        LocalDateTime start = event.getStartDate();
        LocalDateTime end = event.getEndDate();

        if (start == null) return "No date";

        //  Same-day event
        if (end != null && start.toLocalDate().equals(end.toLocalDate())) {
            return start.format(DATE_FORMAT) + " • "
                    + start.format(TIME_FORMAT) + " - "
                    + end.format(TIME_FORMAT);
        }

        //  Multi-day event
        if (end != null) {
            return start.format(DATE_FORMAT) + " "
                    + start.format(TIME_FORMAT) + " → "
                    + end.format(DATE_FORMAT) + " "
                    + end.format(TIME_FORMAT);
        }

        // Only start date
        return start.format(DATE_FORMAT) + " • "
                + start.format(TIME_FORMAT);
    }
}
