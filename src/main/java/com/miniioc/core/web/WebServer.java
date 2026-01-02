package com.miniioc.core.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Logger;

public class WebServer {
    private static final Logger logger = Logger.getLogger(WebServer.class.getName());

    private final Map<String, EndpointHandler> urlMappings;

    public WebServer(Map<String, EndpointHandler> urlMappings) {
        this.urlMappings = urlMappings;
    }

    public void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", this::handleRequest);
            server.start();
        } catch (Exception e) {
            throw new WebServerException("Failed to start WebServer", e);
        }
    }

    private void handleRequest(com.sun.net.httpserver.HttpExchange exchange) {
        String key = exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath();
        EndpointHandler handler = urlMappings.get(key);
        String response;

        if (handler != null) {
            response = invokeHandler(handler);
        } else {
            response = "404 Not Found";
        }

        sendResponse(exchange, response);
    }

    private String invokeHandler(EndpointHandler handler) {
        try {
            Object result = handler.invoke();
            return result.toString();
        } catch (Exception e) {
            logger.severe("Error invoking handler for " + handler.getHttpMethod() + " " + handler.getPath()
                    + ": " + e.getMessage());
            logger.throwing(handler.getClass().getName(), "invokeHandler", e);
            return "500 Internal Server Error";
        }
    }

    private void sendResponse(HttpExchange exchange, String response) {
        try {
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e) {
            throw new WebServerException("Failed to send HTTP response", e);
        }
    }
}