package com.miniioc.framework.context.exception;

public class BeanLifecycleException extends RuntimeException {
    public BeanLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
