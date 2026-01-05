package com.miniioc.core.web.handler;

public class EndpointInvocationException extends RuntimeException {
    public EndpointInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
