package dk.easv.eventticketapp.be.enums;

public enum DiscountType {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("Fixed Amount"),
    FREE("Free");

    private final String displayName;
    DiscountType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
