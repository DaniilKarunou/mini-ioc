package com.miniioc.core.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EndpointHandler {
    private final Object controller;
    private final Method method;
    private final String httpMethod;
    private final String path;

    public EndpointHandler(Object controller, Method method, String httpMethod, String path) {
        this.controller = controller;
        this.method = method;
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public Object invoke() throws EndpointInvocationException {
        try {
            return method.invoke(controller);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new EndpointInvocationException(
                    "Failed to invoke endpoint " + httpMethod + " " + path, e);
        }
    }

    public String getHttpMethod() { return httpMethod; }
    public String getPath() { return path; }
}