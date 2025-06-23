package Service;

import Tugas2.DBConnection;
import model.Booking;
import model.Review;
import model.Villa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VillaService {
    public static List<Villa> getAllVillas() throws SQLException {
        List<Villa> villas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM villas";
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Villa villa = mapResultSetToVilla(rs);
                villas.add(villa);
            }
        }

        return villas;
    }

    public static List<Villa> getVillasById(int villaId) throws SQLException {
        List<Villa> villas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM villas WHERE id="+ villaId;
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Villa villa = mapResultSetToVilla(rs);
                villas.add(villa);
            }
        }

        return villas;
    }

    public static List<Booking> getAllBookedsRoom(int id) throws SQLException {
        List<Booking> bookedRooms = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.* FROM bookings b JOIN room_types rt ON b.room_type = rt.id WHERE rt.villa ="+ id;
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Booking bookedRoom = mapResultSetToBooking(rs);
                bookedRooms.add(bookedRoom);
            }
        }

        return bookedRooms;
    }

    public static List<Review> getReviewsByVillaId(int villaId) throws SQLException {
        List<Review> reviews = new ArrayList<>();

        String sql = """
        SELECT r.*
        FROM reviews r
        JOIN bookings b ON r.booking = b.id
        JOIN room_types rt ON b.room_type = rt.id
        WHERE rt.villa = ?
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, villaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Review review = mapResultSetToReview(rs);
                reviews.add(review);
            }
        }

        return reviews;
    }


    private static Villa mapResultSetToVilla(ResultSet rs) throws SQLException {
        return new Villa(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("address")
        );
    }

    private static Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setCustomer(rs.getInt("customer"));
        booking.setRoomType(rs.getInt("room_type"));
        booking.setCheckinDate(rs.getTimestamp("checkin_date").toLocalDateTime());
        booking.setCheckoutDate(rs.getTimestamp("checkout_date").toLocalDateTime());
        booking.setPrice(rs.getInt("price"));

        int voucherId = rs.getInt("voucher");
        booking.setVoucher(rs.wasNull() ? null : voucherId);

        booking.setFinalPrice(rs.getInt("final_price"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setHasCheckedin(rs.getBoolean("has_checkedin"));
        booking.setHasCheckedout(rs.getBoolean("has_checkedout"));

        return booking;
    }

    private static Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setBooking(rs.getInt("booking"));
        review.setStar(rs.getInt("star"));
        review.setTitle(rs.getString("title"));
        review.setContent(rs.getString("content"));
        return review;
    }
}
