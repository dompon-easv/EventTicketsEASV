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

    @Override
    public VoucherType createVoucherType(VoucherType newType) throws SQLException {
        VoucherType existing = findVoucherType(
                newType.getName(),
                newType.getDiscountValue(),
                newType.getDiscountType()
        );

        if (existing != null) {
            return existing;
        }

        String sql = "INSERT INTO VoucherTypes (name, discountValue, description, discountType) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
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

        throw new SQLException("Creating VoucherType failed");
    }

    @Override
    public Voucher createVoucher(Voucher voucher) throws SQLException {

        String sql = "INSERT INTO Vouchers (voucherTypeId, eventId, uuid, status, createDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, voucher.getVoucherType().getId());

            if (voucher.getEventId() <= 0) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, voucher.getEventId());
            }

            stmt.setString(3, voucher.getUuid());
            stmt.setString(4, voucher.getStatus().name());
            stmt.setDate(5, Date.valueOf(LocalDate.now()));

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                voucher.setId(keys.getInt(1));
                return voucher;
            }
        }
        throw new SQLException("Creating Voucher failed");
    }

    @Override
    public void updateVoucher(Voucher voucher) throws Exception {

        String updateTypeSql =
                "UPDATE VoucherTypes SET name = ?, discountValue = ?, description = ?, discountType = ? WHERE id = ?";

        String updateVoucherSql =
                "UPDATE Vouchers SET eventId = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(updateTypeSql)) {
                stmt.setString(1, voucher.getVoucherType().getName());
                stmt.setDouble(2, voucher.getVoucherType().getDiscountValue());
                stmt.setString(3, voucher.getVoucherType().getDescription());
                stmt.setString(4, voucher.getVoucherType().getDiscountType().name());
                stmt.setInt(5, voucher.getVoucherType().getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateVoucherSql)) {
                if (voucher.getEventId() <= 0) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, voucher.getEventId());
                }
                stmt.setInt(2, voucher.getId());
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Failed to update voucher", e);
        }
    }

    @Override
    public void deleteVoucher(int voucherId) throws Exception {

        String getTypeSql = "SELECT voucherTypeId FROM Vouchers WHERE id = ?";
        String deleteVoucherSql = "DELETE FROM Vouchers WHERE id = ?";
        String deleteTypeSql = "DELETE FROM VoucherTypes WHERE id = ?";
        String checkTypeSql = "SELECT COUNT(*) FROM Vouchers WHERE voucherTypeId = ? AND id <> ?";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            int voucherTypeId;

            // 1. get voucherTypeId
            try (PreparedStatement stmt = conn.prepareStatement(getTypeSql)) {
                stmt.setInt(1, voucherId);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    throw new Exception("Voucher not found");
                }

                voucherTypeId = rs.getInt("voucherTypeId");
            }

            // 2. delete voucher
            try (PreparedStatement stmt = conn.prepareStatement(deleteVoucherSql)) {
                stmt.setInt(1, voucherId);
                stmt.executeUpdate();
            }

            // 3. check if voucherType still used (IMPORTANT FIX: SAME CONNECTION)
            boolean stillUsed;

            try (PreparedStatement stmt = conn.prepareStatement(checkTypeSql)) {
                stmt.setInt(1, voucherTypeId);
                stmt.setInt(2, voucherId);

                ResultSet rs = stmt.executeQuery();
                rs.next();

                stillUsed = rs.getInt(1) > 0;
            }

            // 4. delete type if unused
            if (!stillUsed) {
                try (PreparedStatement stmt = conn.prepareStatement(deleteTypeSql)) {
                    stmt.setInt(1, voucherTypeId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();

        } catch (Exception e) {
            throw new Exception("Failed to delete voucher", e);
        }
    }

    @Override
    public Voucher createSpecialVoucherTransaction(VoucherType newType, int eventId, String uuid) throws Exception {

        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            VoucherType savedType = createVoucherType(newType);

            Voucher newVoucher = new Voucher(
                    0,
                    uuid,
                    eventId,
                    VoucherStatus.ACTIVE,
                    LocalDate.now(),
                    savedType
            );

            Voucher savedVoucher = createVoucher(newVoucher);

            conn.commit();
            return savedVoucher;

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Transaction failed", e);

        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public List<Voucher> getAllVouchers() throws SQLException {

        List<Voucher> list = new ArrayList<>();

        String sql =
                "SELECT v.id AS v_id, v.uuid, v.eventId, v.status, v.createDate, " +
                        "vt.id AS vt_id, vt.name, vt.discountValue, vt.description, vt.discountType " +
                        "FROM Vouchers v " +
                        "JOIN VoucherTypes vt ON v.voucherTypeId = vt.id";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                VoucherType type = new VoucherType(
                        rs.getInt("vt_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discountValue"),
                        DiscountType.valueOf(rs.getString("discountType"))
                );

                int eventId = rs.getInt("eventId");
                if (rs.wasNull()) eventId = 0;

                Voucher voucher = new Voucher(
                        rs.getInt("v_id"),
                        rs.getString("uuid"),
                        eventId,
                        VoucherStatus.valueOf(rs.getString("status")),
                        rs.getDate("createDate").toLocalDate(),
                        type
                );

                list.add(voucher);
            }
        }

        return list;
    }

    public List<Voucher> getVouchersByEventIds(List<Integer> eventIds) throws SQLException {

        List<Voucher> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT v.id AS v_id, " +
                        "v.uuid, " +
                        "v.eventId, " +
                        "e.name AS eventName, " +
                        "v.status, " +
                        "v.createDate, " +
                        "vt.id AS vt_id, " +
                        "vt.name, " +
                        "vt.discountValue, " +
                        "vt.description, " +
                        "vt.discountType " +
                        "FROM Vouchers v " +
                        "JOIN VoucherTypes vt ON v.voucherTypeId = vt.id " +
                        "LEFT JOIN Events e ON v.eventId = e.id " +
                        "WHERE v.eventId IS NULL OR v.eventId IN ("
        );

        for (int i = 0; i < eventIds.size(); i++) {
            sql.append("?");
            if (i < eventIds.size() - 1) sql.append(",");
        }
        sql.append(")");

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int i = 1;
            for (Integer id : eventIds) {
                stmt.setInt(i++, id);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                LocalDate date = null;
                Date sqlDate = rs.getDate("createDate");
                if (sqlDate != null) {
                    date = sqlDate.toLocalDate();
                }

                VoucherType type = new VoucherType(
                        rs.getInt("vt_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discountValue"),
                        DiscountType.valueOf(rs.getString("discountType"))
                );

                int eventId = rs.getInt("eventId");
                if (rs.wasNull()) eventId = 0;

                Voucher voucher = new Voucher(
                        rs.getInt("v_id"),
                        rs.getString("uuid"),
                        eventId,
                        VoucherStatus.valueOf(rs.getString("status")),
                        date,
                        type
                );
                voucher.setEventName(rs.getString("eventName"));
                list.add(voucher);
            }
        }
        return list;
    }

    public VoucherType findVoucherType(String name, double value, DiscountType type) throws SQLException {
        String sql = "SELECT * FROM VoucherTypes WHERE name = ? AND discountValue = ? AND discountType = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, value);
            stmt.setString(3, type.name());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new VoucherType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discountValue"),
                        DiscountType.valueOf(rs.getString("discountType"))
                );
            }
        }
        return null;
    }


}