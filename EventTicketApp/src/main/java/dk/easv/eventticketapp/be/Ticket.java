package dk.easv.eventticketapp.be;

import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private String customerFullName;
    private String customerEmail;
    private String eventName;
    private LocalDateTime startDate;
    private String qrText;

    public Ticket (int ticketId, String customerFullName, String customerEmail, String eventName, LocalDateTime startDate, String qrText ) {
        this.ticketId = ticketId;
        this.customerFullName = customerFullName;
        this.customerEmail = customerEmail;
        this.eventName = eventName;
        this.startDate = startDate;
        this.qrText = qrText;

    }

    public int getTicketId() {
        return ticketId;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
    public String getQrText() {
        return qrText;
    }

}
