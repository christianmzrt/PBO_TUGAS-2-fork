package Service;

import model.Voucher;
import Tugas2.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VoucherService {

    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Voucher> getAllVouchers() throws SQLException {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        }

        return vouchers;
    }

    public Voucher getVoucherById(int id) throws SQLException {
        String sql = "SELECT * FROM vouchers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVoucher(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public Voucher getVoucherByCode(String code) throws SQLException {
        String sql = "SELECT * FROM vouchers WHERE code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVoucher(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public Voucher createVoucher(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO vouchers (code, description, discount, start_date, end_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, voucher.getCode());
            pstmt.setString(2, voucher.getDescription());
            pstmt.setDouble(3, voucher.getDiscount());
            pstmt.setString(4, voucher.getStartDate().format(DB_FORMATTER));
            pstmt.setString(5, voucher.getEndDate().format(DB_FORMATTER));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating voucher failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    voucher.setId(generatedKeys.getInt(1));
                    return voucher;
                } else {
                    throw new SQLException("Creating voucher failed, no ID obtained.");
                }
            }
        }
    }

    public Voucher updateVoucher(int id, Voucher voucher) throws SQLException {
        String sql = "UPDATE vouchers SET code = ?, description = ?, discount = ?, start_date = ?, end_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, voucher.getCode());
            pstmt.setString(2, voucher.getDescription());
            pstmt.setDouble(3, voucher.getDiscount());
            pstmt.setString(4, voucher.getStartDate().format(DB_FORMATTER));
            pstmt.setString(5, voucher.getEndDate().format(DB_FORMATTER));
            pstmt.setInt(6, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return null;
            }

            voucher.setId(id);
            return voucher;
        }
    }

    public boolean deleteVoucher(int id) throws SQLException {
        String sql = "DELETE FROM vouchers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean isVoucherCodeExists(String code, Integer excludeId) throws SQLException {
        String sql = excludeId != null ?
                "SELECT COUNT(*) FROM vouchers WHERE code = ? AND id != ?" :
                "SELECT COUNT(*) FROM vouchers WHERE code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            if (excludeId != null) {
                pstmt.setInt(2, excludeId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean isVoucherValid(String code, LocalDateTime bookingDate) throws SQLException {
        Voucher voucher = getVoucherByCode(code);
        if (voucher == null) {
            return false;
        }

        return bookingDate.isAfter(voucher.getStartDate()) &&
                bookingDate.isBefore(voucher.getEndDate());
    }

    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setId(rs.getInt("id"));
        voucher.setCode(rs.getString("code"));
        voucher.setDescription(rs.getString("description"));
        voucher.setDiscount(rs.getDouble("discount"));

        String startDateStr = rs.getString("start_date");
        if (startDateStr != null) {
            voucher.setStartDate(LocalDateTime.parse(startDateStr, DB_FORMATTER));
        }

        String endDateStr = rs.getString("end_date");
        if (endDateStr != null) {
            voucher.setEndDate(LocalDateTime.parse(endDateStr, DB_FORMATTER));
        }

        voucher.setStartDate(LocalDateTime.parse(startDateStr, DB_FORMATTER));
        voucher.setEndDate(LocalDateTime.parse(endDateStr, DB_FORMATTER));

        return voucher;
    }
}