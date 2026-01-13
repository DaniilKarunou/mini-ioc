package com.miniioc.framework.web.exception;

public record ErrorResponse(
        int status,
        String message,
        String error,
        String path
) {}
