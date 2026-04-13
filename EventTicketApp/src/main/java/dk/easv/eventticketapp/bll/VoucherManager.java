package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.dao.IVoucherDAO;
import dk.easv.eventticketapp.dao.VoucherDAO;
import java.util.List;
import java.util.UUID;

public class VoucherManager {
    private IVoucherDAO voucherDAO;

    public VoucherManager() {
        this.voucherDAO = new VoucherDAO();
    }

    public List<Voucher> getAllVouchers() throws Exception {
        return voucherDAO.getAllVouchers();
    }

    public Voucher createSpecialVoucher(String name, String desc, double val, DiscountType dType, int eventId) throws Exception {
        // Business Logic: Generate a fresh UUID for every new voucher
        String uniqueID = UUID.randomUUID().toString();

        VoucherType newType = new VoucherType(-1, name, desc, val, dType);
        return voucherDAO.createSpecialVoucherTransaction(newType, eventId, uniqueID);
    }
}