package com.htb.tiktok;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TikTokServer {

    private final HttpServer server;
    private final ConcurrentLinkedQueue<TikTokEvent> eventQueue;

    public TikTokServer(ConcurrentLinkedQueue<TikTokEvent> eventQueue, int port) throws Exception {
        this.eventQueue = eventQueue;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/donation", new DonationHandler());
        server.setExecutor(java.util.concurrent.Executors.newSingleThreadExecutor());
    }

    public void start() {
        server.start();
        System.out.println("TikTok Server escuchando en puerto " + server.getAddress().getPort());
    }

    public void stop() {
        server.stop(0);
    }

    private class DonationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    String response = "Solo POST permitido";
                    exchange.sendResponseHeaders(405, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                    return;
                }

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
                reader.close();

                String json = body.toString();
                System.out.println("JSON recibido: '" + json + "'");

                TikTokEvent event = parseEvent(json);

                if (event != null) {
                    eventQueue.offer(event);
                    System.out.println("Donación recibida: " + event);
                    String response = "OK";
                    exchange.getResponseHeaders().set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    System.err.println("Error: no se pudo parsear la donación");
                    String error = "JSON inválido";
                    exchange.sendResponseHeaders(400, error.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes(StandardCharsets.UTF_8));
                    }
                }

            } catch (Exception e) {
                System.err.println("Error procesando donación: " + e.getMessage());
                try {
                    String error = "Error";
                    exchange.sendResponseHeaders(500, error.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception ignored) {}
            }
        }

        private TikTokEvent parseEvent(String json) {
            try {
                json = json.replaceAll("\\s+", "");
                String user = extractJsonValue(json, "user");
                String type = extractJsonValue(json, "type");

                if (user == null) return null;
                if (type == null) type = "coin";

                return new TikTokEvent(user, type);
            } catch (Exception e) {
                return null;
            }
        }

        private String extractJsonValue(String json, String key) {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search);
            boolean quoted;
            if (start == -1) {
                search = "\"" + key + "\":";
                start = json.indexOf(search);
                if (start == -1) return null;
                start += search.length();
                quoted = start < json.length() && json.charAt(start) == '"';
                if (quoted) start++;
            } else {
                start += search.length();
                quoted = true;
            }
            int end;
            if (quoted) {
                end = json.indexOf("\"", start);
            } else {
                end = json.indexOf(",", start);
                if (end == -1) end = json.indexOf("}", start);
            }
            if (end == -1) return null;
            return quoted ? json.substring(start, end) : json.substring(start, end);
        }
    }
}
