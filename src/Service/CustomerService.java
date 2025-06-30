package Service;

import Tugas2.DBConnection;
import model.Customer;
import model.Booking;
import model.Review;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        }

        return customers;
    }

    public static Customer getCustomerById(int customerId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public static List<Booking> getCustomerBookings(int customerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM bookings WHERE customer = ?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookings.add(booking);
            }
        }

        return bookings;
    }

    public static List<Review> getCustomerReviews(int customerId) throws SQLException {
        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.* FROM reviews r " +
                    "JOIN bookings b ON r.booking = b.id " +
                    "WHERE b.customer = ?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = mapResultSetToReview(rs);
                reviews.add(review);
            }
        }

        return reviews;
    }

    public static void createCustomer(String name, String email, String phone) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM customers WHERE email = ?";
            var checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            var checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                throw new SQLException("Email sudah terdaftar");
            }

            String sql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();
        }
    }

    public static boolean updateCustomer(int customerId, String name, String email, String phone) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM customers WHERE email = ? AND id != ?";
            var checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            checkStmt.setInt(2, customerId);
            var checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                throw new SQLException("Email sudah digunakan customer lain");
            }

            String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setInt(4, customerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static int createBooking(int customerId, int roomTypeId, String checkinDate,
                                    String checkoutDate, Integer voucherId) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime checkin, checkout;

        try {
            checkin = LocalDateTime.parse(checkinDate, formatter);
            checkout = LocalDateTime.parse(checkoutDate, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal harus: YYYY-MM-DD HH:mm:ss");
        }

        if (!checkout.isAfter(checkin)) {
            throw new IllegalArgumentException("Tanggal checkout harus setelah checkin");
        }

        try (Connection conn = DBConnection.getConnection()) {
            String checkCustomerSql = "SELECT COUNT(*) FROM customers WHERE id = ?";
            var checkCustomerStmt = conn.prepareStatement(checkCustomerSql);
            checkCustomerStmt.setInt(1, customerId);
            var customerRs = checkCustomerStmt.executeQuery();

            if (!customerRs.next() || customerRs.getInt(1) == 0) {
                throw new IllegalArgumentException("Customer tidak ditemukan");
            }

            String checkRoomSql = "SELECT price FROM room_types WHERE id = ?";
            var checkRoomStmt = conn.prepareStatement(checkRoomSql);
            checkRoomStmt.setInt(1, roomTypeId);
            var roomRs = checkRoomStmt.executeQuery();

            if (!roomRs.next()) {
                throw new IllegalArgumentException("Tipe kamar tidak ditemukan");
            }

            int roomPrice = roomRs.getInt("price");
            int finalPrice = roomPrice;

            if (voucherId != null) {
                String checkVoucherSql = "SELECT discount, start_date, end_date FROM vouchers WHERE id = ?";
                var checkVoucherStmt = conn.prepareStatement(checkVoucherSql);
                checkVoucherStmt.setInt(1, voucherId);
                var voucherRs = checkVoucherStmt.executeQuery();

                if (voucherRs.next()) {
                    double discount = voucherRs.getDouble("discount");
                    LocalDateTime voucherStart = LocalDateTime.parse(voucherRs.getString("start_date"), formatter);
                    LocalDateTime voucherEnd = LocalDateTime.parse(voucherRs.getString("end_date"), formatter);
                    LocalDateTime now = LocalDateTime.now();

                    if (now.isBefore(voucherStart) || now.isAfter(voucherEnd)) {
                        throw new IllegalArgumentException("Voucher tidak berlaku pada waktu ini");
                    }

                    finalPrice = (int)(roomPrice * (1 - discount));
                } else {
                    throw new IllegalArgumentException("Voucher tidak ditemukan");
                }
            }

            String insertSql = "INSERT INTO bookings (customer, room_type, checkin_date, checkout_date, price, voucher, final_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            var insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, customerId);
            insertStmt.setInt(2, roomTypeId);
            insertStmt.setString(3, checkinDate);
            insertStmt.setString(4, checkoutDate);
            insertStmt.setInt(5, roomPrice);
            if (voucherId != null) {
                insertStmt.setInt(6, voucherId);
            } else {
                insertStmt.setNull(6, Types.INTEGER);
            }
            insertStmt.setInt(7, finalPrice);

            insertStmt.executeUpdate();

            var generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }

            throw new SQLException("Gagal mendapatkan ID booking");
        }
    }

    public static void createReview(int customerId, int bookingId, int star, String title, String content) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String checkBookingSql = "SELECT customer, has_checkedout FROM bookings WHERE id = ?";
            var checkBookingStmt = conn.prepareStatement(checkBookingSql);
            checkBookingStmt.setInt(1, bookingId);
            var bookingRs = checkBookingStmt.executeQuery();

            if (!bookingRs.next()) {
                throw new IllegalArgumentException("Booking tidak ditemukan");
            }

            if (bookingRs.getInt("customer") != customerId) {
                throw new IllegalArgumentException("Booking ini bukan milik customer");
            }

            if (bookingRs.getInt("has_checkedout") == 0) {
                throw new IllegalArgumentException("Review hanya bisa diberikan setelah checkout");
            }

            String checkReviewSql = "SELECT COUNT(*) FROM reviews WHERE booking = ?";
            var checkReviewStmt = conn.prepareStatement(checkReviewSql);
            checkReviewStmt.setInt(1, bookingId);
            var reviewRs = checkReviewStmt.executeQuery();

            if (reviewRs.next() && reviewRs.getInt(1) > 0) {
                throw new IllegalArgumentException("Review untuk booking ini sudah ada");
            }

            String insertSql = "INSERT INTO reviews (booking, star, title, content) VALUES (?, ?, ?, ?)";
            var insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, bookingId);
            insertStmt.setInt(2, star);
            insertStmt.setString(3, title);
            insertStmt.setString(4, content);
            insertStmt.executeUpdate();
        }
    }

    private static Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }

    private static Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            Object voucherObj = rs.getObject("voucher");
            Integer voucher = voucherObj != null ? ((Number) voucherObj).intValue() : null;

            return new Booking(
                    rs.getInt("id"),
                    rs.getInt("customer"),
                    rs.getInt("room_type"),
                    LocalDateTime.parse(rs.getString("checkin_date"), formatter),
                    LocalDateTime.parse(rs.getString("checkout_date"), formatter),
                    rs.getInt("price"),
                    voucher,
                    rs.getInt("final_price"),
                    rs.getString("payment_status"),
                    rs.getInt("has_checkedin") == 1,
                    rs.getInt("has_checkedout") == 1
            );
        } catch (Exception e) {
            throw new SQLException("Gagal mapping Booking: " + e.getMessage());
        }
    }


    private static Review mapResultSetToReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getInt("booking"),
                rs.getInt("star"),
                rs.getString("title"),
                rs.getString("content")
        );
    }

    public static boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        }
    }
}