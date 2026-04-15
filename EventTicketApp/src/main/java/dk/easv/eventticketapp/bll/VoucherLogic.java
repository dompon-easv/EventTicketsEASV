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

        validateVoucher(value, type);

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
        validateVoucher(
                voucher.getVoucherType().getDiscountValue(),
                voucher.getVoucherType().getDiscountType()
        );
        voucherDAO.updateVoucher(voucher);
    }

    public void deleteVoucher(int voucherId) throws Exception {
        voucherDAO.deleteVoucher(voucherId);
    }

    private void validateVoucher(double value, DiscountType type) {

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
                value = 0;
                break;
        }
    }
}
