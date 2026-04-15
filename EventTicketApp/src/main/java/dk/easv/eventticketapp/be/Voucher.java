package dk.easv.eventticketapp.be;

import dk.easv.eventticketapp.be.enums.VoucherStatus;

import java.time.LocalDate;
import java.util.UUID;

public class Voucher {
    private int id;
    private String uuid;
    private int eventId; // 0 if valid for all events
    private VoucherStatus status;
    private LocalDate createdDate;
    private VoucherType voucherType;
    private String eventName;

    public Voucher(int id, String uuid, int eventId, VoucherStatus status, LocalDate createdDate, VoucherType voucherType) {
        this.id = id;
        this.uuid = uuid;
        this.eventId = eventId;
        this.status = status;
        this.createdDate = createdDate;
        this.voucherType = voucherType;
    }


    public int getId() {return id;}
    public String getUuid() {return uuid;}
    public int getEventId() {return eventId;}
    public VoucherStatus getStatus() {return status;}
    public LocalDate getCreatedDate() {return createdDate;}
    public VoucherType getVoucherType() {return voucherType;}
    public String getVoucherName(){
        return voucherType != null ? voucherType.getName() : "Unknown";
    }
    public void setId(int id) {this.id = id;}
    public void setStatus(VoucherStatus status) {this.status = status;}
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
