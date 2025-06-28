package Handler;

import HelperException.ValidationException;
import Service.RoomTypeService;
import Service.VillaService;
import Response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import model.Booking;
import model.Review;
import model.Villa;
import model.Roomtype;
import Tugas2.DBConnection;
import Tugas2.Response;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillaHandler {
    public static boolean handle(HttpExchange httpExchange, String method, String path, Map<String, Object> reqJson, Response res) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            // GET /villas
            if (method.equals("GET") && path.equals("/villas")) {
                String query = httpExchange.getRequestURI().getQuery();

                if (query != null && query.contains("ci_date") && query.contains("co_date")) {
                    try {
                        Map<String, String> queryParams = VillaService.parseQueryParams(query);

                        String ci = queryParams.get("ci_date");
                        String co = queryParams.get("co_date");

                        if (ci == null || co == null) {
                            ApiResponse<Object> errorResponse = ApiResponse.error("Tanggal checkin dan checkout harus diisi");
                            res.setBody(objectMapper.writeValueAsString(errorResponse));
                            res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                            return true;
                        }

                        LocalDate checkin = LocalDate.parse(ci);
                        LocalDate checkout = LocalDate.parse(co);

                        Map<LocalDate, List<Map<String, Object>>> availability = VillaService.getRoomAvailability(checkin, checkout);
                        ApiResponse<Map<LocalDate, List<Map<String, Object>>>> response = ApiResponse.success("Ketersediaan kamar berhasil diambil", availability);
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_OK);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        ApiResponse<Object> errorResponse = ApiResponse.error("Tanggal tidak valid atau terjadi kesalahan");
                        res.setBody(objectMapper.writeValueAsString(errorResponse));
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }
                } else {
                    try {
                        List<Villa> villas = VillaService.getAllVillas();
                        ApiResponse<List<Villa>> response = ApiResponse.success("Data villa berhasil diambil", villas);
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_OK);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        ApiResponse<Object> errorResponse = ApiResponse.error("Gagal mengambil data villa");
                        res.setBody(objectMapper.writeValueAsString(errorResponse));
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                }
            }

            if (method.equals("GET") && path.matches("/villas/\\d+")) {
                int villaId = Integer.parseInt(path.split("/")[2]);
                List<Villa> villas = VillaService.getVillasById(villaId);

                if (villas.isEmpty()) {
                    ApiResponse<Object> response = ApiResponse.error("Villa tidak ditemukan");
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_NOT_FOUND);
                } else {
                    ApiResponse<List<Villa>> response = ApiResponse.success("Data villa berhasil diambil", villas);
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_OK);
                }

                return true;
            }

            // POST /villas
            if (method.equals("POST") && path.equals("/villas")) {
                if (reqJson != null) {
                    try {
                        String name = (String) reqJson.get("name");
                        String description = (String) reqJson.get("description");
                        String address = (String) reqJson.get("address");

                        Villa villa = new Villa(name, description, address);
                        VillaService.addVilla(villa);

                        ApiResponse<Object> response = ApiResponse.success("Villa berhasil ditambahkan");
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_CREATED);
                        return true;

                    } catch (ValidationException e) {
                        ApiResponse<Object> response = ApiResponse.error(e.getMessage());
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        ApiResponse<Object> response = ApiResponse.error("Gagal menyimpan ke database");
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                } else {
                    ApiResponse<Object> response = ApiResponse.error("Data tidak valid");
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            // PUT /villas (ambil id dari body JSON)
            if (method.equals("PUT") && path.matches("/villas/\\d+")) {
                if (reqJson != null) {
                    try {
                        Integer villaId = (Integer) reqJson.get("id");
                        String name = (String) reqJson.get("name");
                        String description = (String) reqJson.get("description");
                        String address = (String) reqJson.get("address");

                        Villa villa = new Villa(name, description, address);
                        boolean updated = VillaService.updateVilla(villaId, villa);

                        if (!updated) {
                            ApiResponse<Object> response = ApiResponse.error("Villa tidak ditemukan");
                            res.setBody(objectMapper.writeValueAsString(response));
                            res.send(HttpURLConnection.HTTP_NOT_FOUND);
                        } else {
                            ApiResponse<Object> response = ApiResponse.success("Villa berhasil diperbarui");
                            res.setBody(objectMapper.writeValueAsString(response));
                            res.send(HttpURLConnection.HTTP_OK);
                        }
                        return true;

                    } catch (ValidationException e) {
                        ApiResponse<Object> response = ApiResponse.error(e.getMessage());
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        ApiResponse<Object> response = ApiResponse.error("Gagal memperbarui villa");
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                } else {
                    ApiResponse<Object> response = ApiResponse.error("Data tidak valid");
                    res.setBody(objectMapper.writeValueAsString(response));
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            if (method.equals("PUT") && path.matches("/villas/\\d+/rooms/\\d+")) {
                if (reqJson != null) {
                    try {
                        int roomTypeId = ((Number) reqJson.get("id")).intValue();
                        int villaId = ((Number) reqJson.get("villa")).intValue();
                        String name = (String) reqJson.get("name");
                        int quantity = ((Number) reqJson.get("quantity")).intValue();
                        int capacity = ((Number) reqJson.get("capacity")).intValue();
                        int price = ((Number) reqJson.get("price")).intValue();
                        String bedSize = (String) reqJson.get("bedSize");
                        boolean hasDesk = Boolean.TRUE.equals(reqJson.get("hasDesk"));
                        boolean hasAc = Boolean.TRUE.equals(reqJson.get("hasAc"));
                        boolean hasTv = Boolean.TRUE.equals(reqJson.get("hasTv"));
                        boolean hasWifi = Boolean.TRUE.equals(reqJson.get("hasWifi"));
                        boolean hasShower = Boolean.TRUE.equals(reqJson.get("hasShower"));
                        boolean hasHotwater = Boolean.TRUE.equals(reqJson.get("hasHotwater"));
                        boolean hasFridge = Boolean.TRUE.equals(reqJson.get("hasFridge"));

                        Roomtype roomtype = new Roomtype(roomTypeId, villaId, name, quantity, capacity, price,
                                bedSize, hasDesk, hasAc, hasTv, hasWifi,
                                hasShower, hasHotwater, hasFridge);

                        boolean updated = VillaService.updateRoomType(roomtype);

                        if (!updated) {
                            res.setBody("{\"error\":\"Villa tidak ditemukan\"}");
                            res.send(HttpURLConnection.HTTP_NOT_FOUND);
                        } else {
                            Map<String, Object> resMap = new HashMap<>();
                            resMap.put("message", "Villa berhasil diperbarui");
                            res.setBody(objectMapper.writeValueAsString(resMap));
                            res.send(HttpURLConnection.HTTP_OK);
                        }
                        return true;

                    } catch (ValidationException e) {
                        res.setBody("{\"error\":\"" + e.getMessage() + "\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal memperbarui villa\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            // DELETE /villas/{id}
            if (method.equals("DELETE") && path.matches("/villas/\\d+")) {
                int villaId = Integer.parseInt(path.split("/")[2]);

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "DELETE FROM villas WHERE id = ?";
                    var pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, villaId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        res.setBody("{\"error\":\"Villa tidak ditemukan\"}");
                        res.send(HttpURLConnection.HTTP_NOT_FOUND);
                    } else {
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Villa berhasil dihapus");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_OK);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal menghapus villa\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if (method.equals("DELETE") && path.matches("/villas/\\d+/rooms/\\d+")) {
                int villaId = Integer.parseInt(path.split("/")[2]);
                int roomsId = Integer.parseInt(path.split("/")[4]);

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "DELETE FROM room_types WHERE id = ? AND villa = ?";
                    var pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, roomsId);
                    pstmt.setInt(2, villaId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        res.setBody("{\"error\":\"Room atau Villa tidak ditemukan\"}");
                        res.send(HttpURLConnection.HTTP_NOT_FOUND);
                    } else {
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Room berhasil dihapus");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_OK);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal menghapus Room\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if ("POST".equalsIgnoreCase(method) && path.matches("^/villas/\\d+/rooms$")) {
                try {
                    int villaId = Integer.parseInt(path.split("/")[2]);

                    Roomtype roomtype = new Roomtype(
                            villaId,
                            (String) reqJson.get("name"),
                            ((Number) reqJson.get("quantity")).intValue(),
                            ((Number) reqJson.get("capacity")).intValue(),
                            ((Number) reqJson.get("price")).intValue(),
                            (String) reqJson.get("bedSize"),
                            Boolean.TRUE.equals(reqJson.get("hasDesk")),
                            Boolean.TRUE.equals(reqJson.get("hasAc")),
                            Boolean.TRUE.equals(reqJson.get("hasTv")),
                            Boolean.TRUE.equals(reqJson.get("hasWifi")),
                            Boolean.TRUE.equals(reqJson.get("hasShower")),
                            Boolean.TRUE.equals(reqJson.get("hasHotwater")),
                            Boolean.TRUE.equals(reqJson.get("hasFridge"))
                    );


                    try (Connection conn = DBConnection.getConnection()) {
                        String sql = """
                        INSERT INTO room_types (villa, name, quantity, capacity, price, bed_size,
                        has_desk, has_ac, has_tv, has_wifi, has_shower, has_hotwater, has_fridge)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

                        var pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, roomtype.getVilla());
                        pstmt.setString(2, roomtype.getName());
                        pstmt.setInt(3, roomtype.getQuantity());
                        pstmt.setInt(4, roomtype.getCapacity());
                        pstmt.setInt(5, roomtype.getPrice());
                        pstmt.setString(6, roomtype.getBedSize());
                        pstmt.setBoolean(7, roomtype.isHasDesk());
                        pstmt.setBoolean(8, roomtype.isHasAc());
                        pstmt.setBoolean(9, roomtype.isHasTv());
                        pstmt.setBoolean(10, roomtype.isHasWifi());
                        pstmt.setBoolean(11, roomtype.isHasShower());
                        pstmt.setBoolean(12, roomtype.isHasHotwater());
                        pstmt.setBoolean(13, roomtype.isHasFridge());
                        pstmt.executeUpdate();

                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Roomtype berhasil ditambahkan");
                        res.setBody(objectMapper.writeValueAsString(response));
                        res.send(HttpURLConnection.HTTP_CREATED);
                        return true;
                    }

                } catch (ValidationException e) {
                    res.setBody("{\"error\":\"" + e.getMessage() + "\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal memproses roomtype\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if (method.equals("GET") && path.matches("^/villas/\\d+/roomtype$")) {
                int villaId = Integer.parseInt(path.split("/")[2]);
                List<Roomtype> roomtype = RoomTypeService.getAllRoomTypes(villaId);
                ApiResponse<List<Roomtype>> response = ApiResponse.success("Data villa berhasil diambil", roomtype);
                res.setBody(objectMapper.writeValueAsString(response));
                res.send(HttpURLConnection.HTTP_OK);
                return true;
            }

            if (method.equals("GET") && path.matches("^/villas/\\d+/bookings$")) {
                int villaId = Integer.parseInt(path.split("/")[2]);
                List<Booking> bookedRooms = VillaService.getAllBookedsRoom(villaId);
                ApiResponse<List<Booking>> response = ApiResponse.success("Data villa berhasil diambil", bookedRooms);
                res.setBody(objectMapper.writeValueAsString(response));
                res.send(HttpURLConnection.HTTP_OK);
                return true;
            }

            if (method.equals("PUT") && path.matches("/checkedin/\\d+")){
                int checkedinId = Integer.parseInt(path.split("/")[2]);
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "UPDATE bookings SET has_checkedin = 1 WHERE id = ?";
                    var pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, checkedinId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        res.setBody("{\"error\":\"Checkedin ID tidak ditemukan\"}");
                        res.send(HttpURLConnection.HTTP_NOT_FOUND);
                    } else {
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Berhasil Checkedin");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_OK);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal Checkedin\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if (method.equals("PUT") && path.matches("/checkedout/\\d+")){
                int checkedinId = Integer.parseInt(path.split("/")[2]);
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "UPDATE bookings SET has_checkedout = 1 WHERE id = ?";
                    var pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, checkedinId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        res.setBody("{\"error\":\"Checkedout ID tidak ditemukan\"}");
                        res.send(HttpURLConnection.HTTP_NOT_FOUND);
                    } else {
                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Berhasil Checkedout");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_OK);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal Checkedout\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return true;
                }
            }

            if (method.equals("GET") && path.matches("^/villas/\\d+/reviews$")) {
                int villaId = Integer.parseInt(path.split("/")[2]);
                List<Review> villaReview = VillaService.getReviewsByVillaId(villaId);
                ApiResponse<List<Review>> response = ApiResponse.success("Data Review berhasil diambil", villaReview);
                res.setBody(objectMapper.writeValueAsString(response));
                res.send(HttpURLConnection.HTTP_OK);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Jika tidak cocok dengan path-method villa
    }
}
