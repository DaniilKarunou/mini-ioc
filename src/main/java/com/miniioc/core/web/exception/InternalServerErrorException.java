package com.miniioc.core.web.exception;

public class InternalServerErrorException extends HttpException {
    public InternalServerErrorException(String message, Throwable cause) {
        super(500, message);
        initCause(cause);
    }
}