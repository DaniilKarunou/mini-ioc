package com.miniioc.core.context;

public class BeanCreationException extends Exception {

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
