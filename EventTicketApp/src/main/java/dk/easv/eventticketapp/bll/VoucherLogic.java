package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.dao.EventCoordinatorDAO;
import dk.easv.eventticketapp.dao.VoucherDAO;

import java.util.ArrayList;
import java.util.List;

public class VoucherLogic {

    private final VoucherDAO voucherDAO;
    private final EventCoordinatorDAO eventCoordinatorDAO;

    public VoucherLogic() {
        this.voucherDAO = new VoucherDAO();
        this.eventCoordinatorDAO = new EventCoordinatorDAO();
    }

    public List<Voucher> getVouchersForCoordinator(int userId) throws Exception {

        List<Integer> eventIds = eventCoordinatorDAO.getEventIdsByUser(userId);

        if (eventIds.isEmpty()) {
            return new ArrayList<>();
        }
        return voucherDAO.getVouchersByEventIds(eventIds);
    }

    public Voucher createVoucher(String name,
                                 String description,
                                 double value,
                                 DiscountType type,
                                 int eventId) throws Exception {

        if (type == DiscountType.FREE) {
            value = 0;
        }

        validateVoucherFields(name, description, value, type);
        validateUniqueVoucher(name, value, type, eventId, -1);

        VoucherType voucherType = new VoucherType(
                0,
                name,
                description,
                value,
                type
        );

        String uuid = "V-" + java.util.UUID.randomUUID();

        return voucherDAO.createSpecialVoucherTransaction(
                voucherType,
                eventId,
                uuid
        );
    }

    public void updateVoucher(Voucher voucher) throws Exception {
        VoucherType type = voucher.getVoucherType();

        validateVoucherFields(
                type.getName(),
                type.getDescription(),
                type.getDiscountValue(),
                type.getDiscountType()
        );

        validateUniqueVoucher(
                type.getName(),
                type.getDiscountValue(),
                type.getDiscountType(),
                voucher.getEventId(),
                voucher.getId()
        );
        voucherDAO.updateVoucher(voucher);
    }

    public void deleteVoucher(int voucherId) throws Exception {
        voucherDAO.deleteVoucher(voucherId);
    }

    private void validateVoucherFields(String name,
                                       String description,
                                       double value,
                                       DiscountType type) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Voucher name is required.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Voucher description is required.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Discount type is required.");
        }

        switch (type) {

            case PERCENTAGE:
                if (value <= 0 || value > 100) {
                    throw new IllegalArgumentException("Percentage must be between 1 and 100.");
                }
                break;

            case FIXED_AMOUNT:
                if (value <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than 0.");
                }
                break;

            case FREE:
                break;
        }
    }

    private void validateUniqueVoucher(String name,
                                       double value,
                                       DiscountType type,
                                       int eventId,
                                       int ignoreVoucherId) throws Exception {

        List<Voucher> existing = voucherDAO.getAllVouchers();

        for (Voucher v : existing) {

            // skip current voucher during update
            if (v.getId() == ignoreVoucherId) continue;

            VoucherType vt = v.getVoucherType();

            boolean sameType =
                    vt.getName().equalsIgnoreCase(name)
                            && vt.getDiscountType() == type
                            && Double.compare(vt.getDiscountValue(), value) == 0;

            if (!sameType) continue;

            boolean existingIsGlobal = v.getEventId() == 0;
            boolean newIsGlobal = eventId == 0;

            boolean sameEvent = v.getEventId() == eventId;

            /*
             * 1. EXACT DUPLICATE (same type + same event scope)
             */
            if (sameEvent) {
                throw new IllegalArgumentException(
                        "This voucher already exists for the selected event scope."
                );
            }

            /*
             * 2. GLOBAL vs SPECIFIC EVENT CONFLICTS
             */

            // existing GLOBAL → new specific event
            if (existingIsGlobal && !newIsGlobal) {
                throw new IllegalArgumentException(
                        "A GLOBAL (ALL EVENTS) voucher of this type already exists. You cannot create it for a specific event."
                );
            }

            // existing specific event → new GLOBAL
            if (!existingIsGlobal && newIsGlobal) {
                throw new IllegalArgumentException(
                        "A voucher of this type already exists for a specific event. You cannot create a GLOBAL version."
                );
            }
        }
    }
}