package com.miniioc.core.lifecycle;

public class BeanLifecycleException extends RuntimeException {
    public BeanLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
