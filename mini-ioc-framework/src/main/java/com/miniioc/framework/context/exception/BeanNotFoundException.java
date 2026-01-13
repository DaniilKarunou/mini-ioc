package com.miniioc.framework.context.exception;

public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException(Class<?> clazz) {
        super("Bean not registered: " + clazz.getName());
    }
}

