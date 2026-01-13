package com.miniioc.framework.web.exception;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public boolean supports(Exception ex) {
        return true;
    }

    @Override
    public void handle(Exception ex, HttpExchange exchange) {
        int status;
        String body;

        if (ex instanceof HttpException he) {
            status = he.getStatus();
            body = he.getBody();
        } else {
            status = 500;
            body = "Internal Server Error";
        }

        try {
            exchange.sendResponseHeaders(status, body.getBytes(StandardCharsets.UTF_8).length);
            try (var os = exchange.getResponseBody()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
