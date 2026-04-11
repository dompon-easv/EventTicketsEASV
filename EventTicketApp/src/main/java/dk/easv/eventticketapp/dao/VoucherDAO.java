package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Voucher;
import dk.easv.eventticketapp.be.VoucherType;
import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.be.enums.VoucherStatus;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO implements IVoucherDAO {


        public VoucherType createVoucherType(VoucherType newType) throws SQLException {
            String sql = "INSERT INTO VoucherTypes (name, discountValue, description, discountType) VALUES (?, ?, ?, ?)";

            try (   Connection conn = ConnectionManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, newType.getName());
                stmt.setDouble(2, newType.getDiscountValue());
                stmt.setString(3, newType.getDescription());
                stmt.setString(4, newType.getDiscountType().name());
                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    newType.setId(keys.getInt(1));
                    return newType;
                }
            }
            throw new SQLException("Creating VoucherType failed, no ID obtained.");
        }


    // --------------------------------------------------------
        // METHOD 2: Only handles Vouchers (Reusable!)
        // --------------------------------------------------------
        public Voucher createVoucher(Voucher voucher) throws SQLException {
            String sql = "INSERT INTO Vouchers (voucherTypeId, eventId, uuid, status, createdDate) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, voucher.getVoucherType().getId());

                if (voucher.getEventId() <= 0) {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                } else {
                    stmt.setInt(2, voucher.getEventId());
                }

                stmt.setString(3, voucher.getUuid());
                stmt.setString(4, voucher.getStatus().name());
                stmt.setDate(5, java.sql.Date.valueOf(voucher.getCreatedDate()));
                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    voucher.setId(keys.getInt(1));
                    return voucher;
                }
            }
            throw new SQLException("Creating Voucher failed, no ID obtained.");
        }

        public Voucher createSpecialVoucherTransaction(VoucherType newType, int eventId, String uuid) throws Exception {
            Connection conn = null;
            try {
                conn = ConnectionManager.getConnection();

                conn.setAutoCommit(false);


                VoucherType savedType = createVoucherType(newType);


                Voucher newVoucher = new Voucher(0, uuid, eventId, VoucherStatus.ACTIVE, LocalDate.now(), savedType);
                Voucher savedVoucher = createVoucher(newVoucher);


                conn.commit();
                return savedVoucher;

            } catch (SQLException e) {

                if (conn != null) {
                    conn.rollback();
                }
                throw new Exception("Transaction failed. Database rolled back.", e);
            } finally {

                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
        }

    public List<Voucher> getAllVouchers() throws SQLException {
        List<Voucher> allVouchers = new ArrayList<>();
        String sql = "SELECT v.id AS v_id, v.uuid, v.eventId, v.status, v.createdDate, " +
                "vt.id AS vt_id, vt.name, vt.discountValue, vt.description, vt.discountType " +
                "FROM Vouchers v JOIN VoucherTypes vt ON v.voucherTypeId = vt.id";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VoucherType type = new VoucherType(
                        rs.getInt("vt_id"), rs.getString("name"), rs.getString("description"),
                        rs.getDouble("discountValue"), DiscountType.valueOf(rs.getString("discountType"))
                );

                int eventId = rs.getInt("eventId");
                if (rs.wasNull()) eventId = 0;

                Voucher voucher = new Voucher(
                        rs.getInt("v_id"), rs.getString("uuid"), eventId,
                        VoucherStatus.valueOf(rs.getString("status")),
                        rs.getDate("createdDate").toLocalDate(), type
                );
                allVouchers.add(voucher);
            }
        }
        return allVouchers;
    }
    }
