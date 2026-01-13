package com.miniioc.framework.web.server;

import com.miniioc.framework.web.RequestContext;
import com.miniioc.framework.web.exception.ErrorResponse;
import com.miniioc.framework.web.exception.ExceptionHandler;
import com.miniioc.framework.web.exception.NotFoundException;
import com.miniioc.framework.web.handler.EndpointHandler;
import com.miniioc.framework.util.json.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebServer {

    private final List<EndpointHandler> urlMappings;
    private final List<ExceptionHandler> exceptionHandlers;

    public WebServer(List<EndpointHandler> urlMappings,
                     List<ExceptionHandler> exceptionHandlers) {
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
        RequestContext ctx = null;

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            EndpointHandler matchedHandler = null;
            Map<String, String> pathVariables = Map.of();

            for (EndpointHandler handler : urlMappings) {
                if (handler.matches(method, path)) {
                    matchedHandler = handler;
                    pathVariables = handler.extractPathVariables(path);
                    break;
                }
            }

            if (matchedHandler == null) {
                throw new NotFoundException("Endpoint not found: " + method + " " + path);
            }

            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            Map<String, String> headers = exchange.getRequestHeaders()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> String.join(",", e.getValue())
                    ));

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            ctx = new RequestContext(
                    method,
                    path,
                    pathVariables,
                    queryParams,
                    headers,
                    body
            );

            Object result = matchedHandler.invoke(ctx);
            writeResponse(exchange, 200, result);

        } catch (Exception ex) {
            handleException(exchange, ex, ctx);
        }
    }

    private void handleException(HttpExchange exchange, Exception ex, RequestContext ctx) {
        for (ExceptionHandler handler : exceptionHandlers) {
            if (handler.supports(ex)) {
                ErrorResponse error = handler.handle(ex, ctx);
                writeResponse(exchange, error.status(), error);
                return;
            }
        }
    }

    private void writeResponse(HttpExchange exchange, int status, Object body) {
        try {
            String json = body != null ? JsonMapper.toJson(body) : "";
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (var os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            throw new ResponseWriteException("Failed to write HTTP response", e);
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
}