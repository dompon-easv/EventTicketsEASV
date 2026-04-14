package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;

import java.sql.SQLException;
import java.util.List;

public interface IVoucherDAO {
    VoucherType createVoucherType(VoucherType newType) throws SQLException;
    Voucher createVoucher(Voucher voucher) throws SQLException;
    Voucher createSpecialVoucherTransaction(VoucherType newType, int eventId, String uuid) throws Exception;
    List<Voucher> getAllVouchers() throws SQLException;
}
