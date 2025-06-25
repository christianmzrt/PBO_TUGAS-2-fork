package Service;

import Tugas2.DBConnection;
import model.Booking;
import model.Review;
import model.Villa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

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

    public static void addVilla(Villa villa) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO villas (name, description, address) VALUES (?, ?, ?)";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, villa.getName());
            pstmt.setString(2, villa.getDescription());
            pstmt.setString(3, villa.getAddress());
            pstmt.executeUpdate();
        }
    }

    public static boolean updateVilla(int id, Villa villa) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE villas SET name = ?, description = ?, address = ? WHERE id = ?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, villa.getName());
            pstmt.setString(2, villa.getDescription());
            pstmt.setString(3, villa.getAddress());
            pstmt.setInt(4, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
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

    public static Map<LocalDate, List<Map<String, Object>>> getRoomAvailability(LocalDate checkin, LocalDate checkout) throws SQLException {
        Map<LocalDate, List<Map<String, Object>>> availabilityPerDate = new LinkedHashMap<>();

        List<LocalDate> dates = generateDateRange(checkin, checkout.minusDays(1));

        try (Connection conn = DBConnection.getConnection()) {
            for (LocalDate date : dates) {
                String sql = """
                        SELECT rt.id AS room_type_id, v.name AS villa_name, rt.quantity,
                               COUNT(b.id) AS booked_count,
                               (rt.quantity - COUNT(b.id)) AS available
                        FROM room_types rt
                        JOIN villas v ON rt.villa = v.id
                        LEFT JOIN bookings b ON b.room_type = rt.id
                          AND b.checkin_date <= ? AND b.checkout_date > ?
                        GROUP BY rt.id
                        HAVING available > 0
                        """;

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, date.toString());
                    stmt.setString(2, date.toString());
                    ResultSet rs = stmt.executeQuery();

                    List<Map<String, Object>> roomList = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> room = new HashMap<>();
                        room.put("room_type_id", rs.getInt("room_type_id"));
                        room.put("villa", rs.getString("villa_name"));
                        room.put("available", rs.getInt("available"));
                        roomList.add(room);
                    }

                    availabilityPerDate.put(date, roomList);
                }
            }
        }

        return availabilityPerDate;
    }

    private static List<LocalDate> generateDateRange(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }
}
