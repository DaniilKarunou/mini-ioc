package com.miniioc.core.web.exception;

public class NotFoundException extends HttpException {
    public NotFoundException(String message) {
        super(404, message);
    }
}