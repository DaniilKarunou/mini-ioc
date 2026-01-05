package com.miniioc.core.context.exception;

public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException(Class<?> clazz) {
        super("Bean not registered: " + clazz.getName());
    }
}

