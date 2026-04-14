package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.dao.VoucherDAO;

import java.util.List;

public class VoucherLogic {

    private final VoucherDAO voucherDAO;

    public VoucherLogic() {
        this.voucherDAO = new VoucherDAO();
    }

    public Voucher createVoucher(String name,
                                 String description,
                                 double value,
                                 DiscountType type,
                                 int eventId) throws Exception {

        // 🔥 FIX: handle FREE properly
        if (type == DiscountType.FREE) {
            value = 0;
        }

        // VALIDATION
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

    public List<Voucher> getAllVouchers() throws Exception {
        return voucherDAO.getAllVouchers();
    }
}
