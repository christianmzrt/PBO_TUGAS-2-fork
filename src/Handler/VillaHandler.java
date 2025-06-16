package Handler;

import Service.VillaService;
import Response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import model.Villa;
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Jika tidak cocok dengan path-method villa
    }
}
