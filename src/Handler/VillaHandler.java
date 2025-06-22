package Handler;

import Service.RoomTypeService;
import Service.VillaService;
import Response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Request;
import model.Villa;
import model.Roomtype;
import Tugas2.DBConnection;
import Tugas2.Response;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillaHandler {
    public static boolean handle(HttpExchange httpExchange, String method, String path, Map<String, Object> reqJson, Response res) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // GET /villas
            if (method.equals("GET") && path.equals("/villas")) {
                List<Villa> villas = VillaService.getAllVillas();
                ApiResponse<List<Villa>> response = ApiResponse.success("Data villa berhasil diambil", villas);
                res.setBody(objectMapper.writeValueAsString(response));
                res.send(HttpURLConnection.HTTP_OK);
                return true;
            }

            // POST /villas
            if (method.equals("POST") && path.equals("/villas")) {
                if (reqJson != null) {
                    String name = (String) reqJson.get("name");
                    String description = (String) reqJson.get("description");
                    String address = (String) reqJson.get("address");

                    try (Connection conn = DBConnection.getConnection()) {
                        String sql = "INSERT INTO villas (name, description, address) VALUES (?, ?, ?)";
                        var pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, name);
                        pstmt.setString(2, description);
                        pstmt.setString(3, address);
                        pstmt.executeUpdate();

                        Map<String, Object> resMap = new HashMap<>();
                        resMap.put("message", "Villa berhasil ditambahkan");
                        res.setBody(objectMapper.writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_CREATED);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal menyimpan ke database\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                        return true;
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                    return true;
                }
            }

            // PUT /villas (ambil id dari body JSON)
            if (method.equals("PUT") && path.matches("/villas/\\d+")) {
                if (reqJson != null) {
                    Integer villaId = (Integer) reqJson.get("id");
                    String name = (String) reqJson.get("name");
                    String description = (String) reqJson.get("description");
                    String address = (String) reqJson.get("address");

                    if (villaId == null || name == null || description == null || address == null ||
                            name.isBlank() || description.isBlank() || address.isBlank()) {
                        res.setBody("{\"error\": \"Semua data harus lengkap dan id harus ada\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return true;
                    }

                    try (Connection conn = DBConnection.getConnection()) {
                        String sql = "UPDATE villas SET name = ?, description = ?, address = ? WHERE id = ?";
                        var pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, name);
                        pstmt.setString(2, description);
                        pstmt.setString(3, address);
                        pstmt.setInt(4, villaId);

                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected == 0) {
                            res.setBody("{\"error\":\"Villa tidak ditemukan\"}");
                            res.send(HttpURLConnection.HTTP_NOT_FOUND);
                        } else {
                            Map<String, Object> resMap = new HashMap<>();
                            resMap.put("message", "Villa berhasil diperbarui");
                            res.setBody(objectMapper.writeValueAsString(resMap));
                            res.send(HttpURLConnection.HTTP_OK);
                        }
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

            if ("POST".equalsIgnoreCase(method) && path.matches("^/villas/\\d+/roomtype$")) {
                try {
                    int villaId = Integer.parseInt(path.split("/")[2]);

                    Roomtype roomtype = new Roomtype();
                    roomtype.setVilla(villaId);

                    roomtype.setName((String) reqJson.get("name"));
                    roomtype.setQuantity(((Number) reqJson.get("quantity")).intValue());
                    roomtype.setCapacity(((Number) reqJson.get("capacity")).intValue());
                    roomtype.setPrice(((Number) reqJson.get("price")).intValue());
                    roomtype.setBedSize((String) reqJson.get("bedSize"));
                    roomtype.setHasDesk(Boolean.TRUE.equals(reqJson.get("hasDesk")));
                    roomtype.setHasAc(Boolean.TRUE.equals(reqJson.get("hasAc")));
                    roomtype.setHasTv(Boolean.TRUE.equals(reqJson.get("hasTv")));
                    roomtype.setHasWifi(Boolean.TRUE.equals(reqJson.get("hasWifi")));
                    roomtype.setHasShower(Boolean.TRUE.equals(reqJson.get("hasShower")));
                    roomtype.setHasHotwater(Boolean.TRUE.equals(reqJson.get("hasHotwater")));
                    roomtype.setHasFridge(Boolean.TRUE.equals(reqJson.get("hasFridge")));


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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Jika tidak cocok dengan path-method villa
    }
}
