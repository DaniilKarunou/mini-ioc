package com.miniioc.core.context;

public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException(Class<?> clazz) {
        super("Bean not registered: " + clazz.getName());
    }
}

