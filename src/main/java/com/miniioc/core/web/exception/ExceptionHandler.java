package com.miniioc.core.web.exception;

import com.sun.net.httpserver.HttpExchange;

public interface ExceptionHandler {
    boolean supports(Exception ex);
    void handle(Exception ex, HttpExchange exchange);
}
