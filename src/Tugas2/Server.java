package Tugas2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import Handler.VillaHandler;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private HttpServer server;

    public Server(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RequestHandler());
        server.setExecutor(null); // default executor
        server.start();
        System.out.println("Server started on port " + port);
    }

    private static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            processHttpExchange(httpExchange);
        }
    }

    public static void processHttpExchange(HttpExchange httpExchange) {
        Request req = new Request(httpExchange);
        Response res = new Response(httpExchange);
        ObjectMapper objectMapper = new ObjectMapper();

        URI uri = httpExchange.getRequestURI();
        String method = httpExchange.getRequestMethod();
        String path = uri.getPath();
        System.out.printf("path: %s | method: %s\n", path, method);

        try {
            Map<String, Object> requestBody = null;
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                requestBody = req.getJSON();
            }

            if (VillaHandler.handle(httpExchange, method, path, requestBody, res)) {
                return;
            }

            res.setBody("{\"error\": \"Endpoint tidak ditemukan\"}");
            res.send(HttpURLConnection.HTTP_NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                res.setBody("{\"error\": \"Terjadi kesalahan pada server\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!res.isSent()) {
            try {
                Map<String, Object> resJsonMap = new HashMap<>();
                resJsonMap.put("message", "Request Success");
                String resJson = objectMapper.writeValueAsString(resJsonMap);
                res.setBody(resJson);
                res.send(HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        httpExchange.close();
    }
}
