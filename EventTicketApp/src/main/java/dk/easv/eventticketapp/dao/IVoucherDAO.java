package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;

import java.sql.SQLException;
import java.util.List;

public interface IVoucherDAO {
    Voucher createVoucher(Voucher voucher) throws SQLException;
    VoucherType createVoucherType(VoucherType voucherType)  throws SQLException;
    List<Voucher> getAllVouchers() throws SQLException;
}
