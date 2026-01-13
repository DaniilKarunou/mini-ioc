package com.miniioc.framework.web.server;

import com.miniioc.framework.web.exception.ExceptionHandler;
import com.miniioc.framework.web.exception.NotFoundException;
import com.miniioc.framework.web.handler.EndpointHandler;
import com.miniioc.framework.web.RequestContext;
import com.miniioc.framework.util.json.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class WebServer {
    private final List<EndpointHandler> urlMappings;
    private final List<ExceptionHandler> exceptionHandlers;

    public WebServer(List<EndpointHandler> urlMappings, List<ExceptionHandler> exceptionHandlers) {
        this.urlMappings = urlMappings;
        this.exceptionHandlers = exceptionHandlers;
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

    private void handleRequest(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();

            EndpointHandler matchedHandler = null;
            Map<String, String> pathVariables = Map.of();

            for (EndpointHandler handler : urlMappings) {
                if (handler.matches(requestMethod, requestPath)) {
                    matchedHandler = handler;
                    pathVariables = handler.extractPathVariables(requestPath);
                    break;
                }
            }

            if (matchedHandler == null) {
                throw new NotFoundException("Endpoint not found: " + requestMethod + " " + requestPath);
            }

            String query = exchange.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQueryParams(query);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            RequestContext ctx = new RequestContext(pathVariables, queryParams, body);
            Object result = matchedHandler.invoke(ctx);

            String response = result != null ? JsonMapper.toJson(result) : "";
            sendResponse(exchange, response);

        } catch (Exception ex) {
            handleException(exchange, ex);
        }
    }

    private void handleException(HttpExchange exchange, Exception ex) {
        for (ExceptionHandler handler : exceptionHandlers) {
            if (handler.supports(ex)) {
                handler.handle(ex, exchange);
                return;
            }
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new java.util.HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String param : query.split("&")) {
            String[] parts = param.split("=", 2);
            map.put(parts[0], parts.length > 1 ? parts[1] : "");
        }
        return map;
    }

    private void sendResponse(HttpExchange exchange, String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        try {
            exchange.sendResponseHeaders(200, bytes.length);
            try (var os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            throw new ResponseWriteException("Failed to write HTTP response", e);
        }
    }
}