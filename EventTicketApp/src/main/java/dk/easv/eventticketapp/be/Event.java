package dk.easv.eventticketapp.be;

import java.time.LocalDateTime;

public class Event {

    private int id;
    private String name;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String locationDescription;

    // Constructor without ID (for insert)
    public Event(String name, String location, LocalDateTime startDate,
                 LocalDateTime endDate, String description, String locationDescription) {
        this.name = name;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.locationDescription = locationDescription;
    }

    // Constructor with ID (from DB)
    public Event(int id, String name, String location, LocalDateTime startDate,
                 LocalDateTime endDate, String description, String locationDescription) {
        this(name, location, startDate, endDate, description, locationDescription);
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }


    // =========================
    // GETTERS
    // =========================
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationDescription() {
        return locationDescription;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
}

