package Handler;

import Service.CustomerService;
import Response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import model.Customer;
import model.Booking;
import model.Review;
import Tugas2.DBConnection;
import Tugas2.Response;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;



public class CustomerHandler {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+62|62|0)[0-9]{8,13}$");

    public static boolean handle(HttpExchange httpExchange, String method, String path, Map<String, Object> reqJson, Response res) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Normalize
        path = path.split("\\?")[0];
        path = path.replaceAll("/$", "");

        System.out.println(">> METHOD: " + method + ", PATH: " + path);

        try {
            if (method.equals("GET") && path.equals("/customers")) {
                List<Customer> customers = CustomerService.getAllCustomers();
                ApiResponse<List<Customer>> response = ApiResponse.success("Data customer berhasil diambil", customers);
                res.setBody(objectMapper.writeValueAsString(response));
                res.send(HttpURLConnection.HTTP_OK);
                return true;
            }

            if (method.equals("GET") && path.matches("/customers/\\d+")) {
                int customerId = Integer.parseInt(path.split("/")[2]);

                try {
                    Customer customer = CustomerService.getCustomerById(customerId);
                    if (customer != null) {
                        ApiResponse<Customer> response = ApiResponse.success("Data customer berhasil diambil", customer);
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_OK);
                    } else {
                        ApiResponse<Object> response = ApiResponse.error("Customer tidak ditemukan");
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_NOT_FOUND);
                    }
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal mengambil data customer\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if (method.equals("GET") && path.matches("/customers/\\d+/bookings")) {
                int customerId = Integer.parseInt(path.split("/")[2]);

                try {
                    List<Booking> bookings = CustomerService.getCustomerBookings(customerId);

                    // Kita ubah Booking ke Map manual biar tanggal aman (tanpa LocalDateTime)
                    List<Map<String, Object>> simpleBookings = new ArrayList<>();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    for (Booking booking : bookings) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", booking.getId());
                        map.put("customer", booking.getCustomer());
                        map.put("roomType", booking.getRoomType());
                        map.put("checkinDate", booking.getCheckinDate() != null ? booking.getCheckinDate().format(formatter) : null);
                        map.put("checkoutDate", booking.getCheckoutDate() != null ? booking.getCheckoutDate().format(formatter) : null);
                        map.put("price", booking.getPrice());
                        map.put("voucher", booking.getVoucher());
                        map.put("finalPrice", booking.getFinalPrice());
                        map.put("paymentStatus", booking.getPaymentStatus());
                        map.put("hasCheckedIn", booking.isHasCheckedin());
                        map.put("hasCheckedOut", booking.isHasCheckedout());
                        simpleBookings.add(map);
                    }

                    ApiResponse<List<Map<String, Object>>> response = ApiResponse.success("Data booking customer berhasil diambil", simpleBookings);
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_OK);
                    return true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal mengambil data booking customer\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }


            if (method.equals("GET") && path.matches("/customers/\\d+/reviews")) {
                int customerId = Integer.parseInt(path.split("/")[2]);

                try {
                    List<Review> reviews = CustomerService.getCustomerReviews(customerId);
                    ApiResponse<List<Review>> response = ApiResponse.success("Data review customer berhasil diambil", reviews);
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_OK);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal mengambil data review customer\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if (method.equals("POST") && path.equals("/customers")) {
                if (reqJson != null) {
                    String name = (String) reqJson.get("name");
                    String email = (String) reqJson.get("email");
                    String phone = (String) reqJson.get("phone");

                    if (name == null || email == null || phone == null ||
                            name.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty()) {
                        res.setBody("{\"error\":\"Semua data harus lengkap (name, email, phone)\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        res.setBody("{\"error\":\"Format email tidak valid\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    if (!PHONE_PATTERN.matcher(phone).matches()) {
                        res.setBody("{\"error\":\"Format nomor telepon tidak valid (gunakan format Indonesia)\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    try {
                        CustomerService.createCustomer(name.trim(), email.trim(), phone.trim());
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Customer berhasil didaftarkan");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_CREATED);
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal menyimpan customer ke database\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            if (method.equals("PUT") && path.matches("/customers/\\d+")) {
                int customerId = Integer.parseInt(path.split("/")[2]);

                if (reqJson != null) {
                    String name = (String) reqJson.get("name");
                    String email = (String) reqJson.get("email");
                    String phone = (String) reqJson.get("phone");

                    if (name == null || email == null || phone == null ||
                            name.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty()) {
                        res.setBody("{\"error\":\"Semua data harus lengkap (name, email, phone)\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        res.setBody("{\"error\":\"Format email tidak valid\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    if (!PHONE_PATTERN.matcher(phone).matches()) {
                        res.setBody("{\"error\":\"Format nomor telepon tidak valid (gunakan format Indonesia)\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    try {
                        boolean updated = CustomerService.updateCustomer(customerId, name.trim(), email.trim(), phone.trim());
                        if (updated) {
                            Map<String, Object> resMap = new HashMap<>();
                            resMap.put("message", "Data customer berhasil diperbarui");
                            res.setBody(objectMapper.writeValueAsString(resMap));
                            res.send(HttpURLConnection.HTTP_OK);
                        } else {
                            res.setBody("{\"error\":\"Customer tidak ditemukan\"}");
                            res.send(HttpURLConnection.HTTP_NOT_FOUND);
                        }
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal memperbarui data customer\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            if (method.equals("POST") && path.matches("/customers/\\d+/bookings")) {
                int customerId = Integer.parseInt(path.split("/")[2]);

                if (reqJson != null) {
                    Integer roomTypeId = (Integer) reqJson.get("room_type");
                    String checkinDate = (String) reqJson.get("checkin_date");
                    String checkoutDate = (String) reqJson.get("checkout_date");
                    Integer voucherId = (Integer) reqJson.get("voucher");

                    if (roomTypeId == null || checkinDate == null || checkoutDate == null ||
                            checkinDate.trim().isEmpty() || checkoutDate.trim().isEmpty()) {
                        res.setBody("{\"error\":\"Data booking tidak lengkap (room_type, checkin_date, checkout_date wajib)\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    try {
                        int bookingId = CustomerService.createBooking(customerId, roomTypeId,
                                checkinDate.trim(), checkoutDate.trim(), voucherId);
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Booking berhasil dibuat");
                        resMap.put("booking_id", bookingId);
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_CREATED);
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal membuat booking: " + e.getMessage() + "\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    } catch (IllegalArgumentException e) {
                        res.setBody("{\"error\":\"" + e.getMessage() + "\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            if (method.equals("POST") && path.matches("/customers/\\d+/bookings/\\d+/reviews")) {
                int customerId = Integer.parseInt(path.split("/")[2]);
                int bookingId = Integer.parseInt(path.split("/")[4]);

                if (reqJson != null) {
                    Integer star = (Integer) reqJson.get("star");
                    String title = (String) reqJson.get("title");
                    String content = (String) reqJson.get("content");

                    if (star == null || title == null || content == null ||
                            title.trim().isEmpty() || content.trim().isEmpty()) {
                        res.setBody("{\"error\":\"Semua data review harus lengkap (star, title, content)\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    if (star < 1 || star > 5) {
                        res.setBody("{\"error\":\"Rating harus antara 1-5\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    try {
                        CustomerService.createReview(customerId, bookingId, star, title.trim(), content.trim());
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Review berhasil ditambahkan");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_CREATED);
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal menambahkan review: " + e.getMessage() + "\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    } catch (IllegalArgumentException e) {
                        res.setBody("{\"error\":\"" + e.getMessage() + "\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (method.equals("DELETE") && path.matches("/customers/\\d+")) {
            int customerId = Integer.parseInt(path.split("/")[2]);

            try {
                boolean deleted = CustomerService.deleteCustomer(customerId);
                if (deleted) {
                    Map<String, Object> resMap = new HashMap<>();
                    resMap.put("message", "Customer berhasil dihapus");
                    res.setBody(objectMapper.writeValueAsString(resMap));
                    res.send(HttpURLConnection.HTTP_OK);
                } else {
                    ApiResponse<Object> response = ApiResponse.error("Customer tidak ditemukan");
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_NOT_FOUND);
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                res.setBody("{\"error\":\"Gagal menghapus customer\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                return true;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }
}