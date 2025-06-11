package Tugas2;

import Service.VillaService;
import Response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Villa;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private HttpServer server;
    private ObjectMapper objectMapper;

    private class RequestHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) {
            Server.processHttpExchange(httpExchange);
        }
    }

    public Server(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 128);
        server.createContext("/", new RequestHandler());
        server.start();
    }

    public static void processHttpExchange(HttpExchange httpExchange) {
        Request req = new Request(httpExchange);
        Response res = new Response(httpExchange);

        ObjectMapper objectMapper = new ObjectMapper();
        URI uri = httpExchange.getRequestURI();
        String method = httpExchange.getRequestMethod();
        String path = uri.getPath();
        System.out.printf("path: %s\n", path);

        try {
            if (method.equals("GET") && path.equals("/villas")) {
                try {
                    List<Villa> villas = VillaService.getAllVillas();
                    ApiResponse<List<Villa>> response = ApiResponse.success("Data villa berhasil diambil", villas);
                    String responseJson = objectMapper.writeValueAsString(response);
                    res.setBody(responseJson);
                    res.send(HttpURLConnection.HTTP_OK);

                } catch (Exception e) {
                    e.printStackTrace();
                    ApiResponse<Object> errorResponse = ApiResponse.error("Gagal mengambil data villa");
                    res.setBody(objectMapper.writeValueAsString(errorResponse));
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            }

            else if (method.equals("POST") && path.equals("/villas")) {
                Map<String, Object> reqJson = req.getJSON();

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

                        res.setBody(new ObjectMapper().writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_CREATED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal menyimpan ke database\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                }
            }

            else if (method.equals("PUT") && path.matches("/villas/\\d+")) {
                int villaId = Integer.parseInt(path.split("/")[2]);
                Map<String, Object> reqJson = req.getJSON();

                if (reqJson != null) {
                    String name = (String) reqJson.get("name");
                    String description = (String) reqJson.get("description");
                    String address = (String) reqJson.get("address");

                    if (name == null || description == null || address == null ||
                            name.isBlank() || description.isBlank() || address.isBlank()) {
                        res.setBody("{\"error\": \"Semua data harus lengkap\"}");
                        res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                        return;
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
                            res.setBody(new ObjectMapper().writeValueAsString(resMap));
                            res.send(HttpURLConnection.HTTP_OK);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.setBody("{\"error\":\"Gagal memperbarui villa\"}");
                        res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                } else {
                    res.setBody("{\"error\":\"Data tidak valid\"}");
                    res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                }
            }

            else if (method.equals("DELETE") && path.matches("/villas/\\d+")) {
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
                        res.setBody(new ObjectMapper().writeValueAsString(resMap));
                        res.send(HttpURLConnection.HTTP_OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    res.setBody("{\"error\":\"Gagal menghapus villa\"}");
                    res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            }

            else {
                res.setBody("{\"error\": \"Endpoint tidak ditemukan\"}");
                res.send(HttpURLConnection.HTTP_NOT_FOUND);
            }

            System.out.println("Done!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (!res.isSent()) {
            Map<String, Object> resJsonMap = new HashMap<>();
            resJsonMap.put("message", "Request Success");

            try {
                String resJson = objectMapper.writeValueAsString(resJsonMap);
                res.setBody(resJson);
                res.send(HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        httpExchange.close();
    }
}