package com.miniioc.core.web.handler;

import com.miniioc.core.web.RequestContext;
import com.miniioc.core.web.resolver.ParameterResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


public class EndpointHandler {

    private final Object controller;
    private final Method method;
    private final String httpMethod;
    private final PathPattern pathPattern;
    private final Map<Class<? extends Annotation>, ParameterResolver> resolvers;

    public EndpointHandler(Object controller, Method method, String httpMethod, String path,
                           Map<Class<? extends Annotation>, ParameterResolver> resolvers) {
        this.controller = controller;
        this.method = method;
        this.httpMethod = httpMethod;
        this.pathPattern = new PathPattern(path);
        this.resolvers = resolvers;
    }

    public boolean matches(String requestMethod, String requestPath) {
        return httpMethod.equalsIgnoreCase(requestMethod) && pathPattern.matches(requestPath);
    }

    public Object invoke(RequestContext ctx) throws EndpointInvocationException {
        try {
            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                Parameter p = params[i];
                args[i] = resolvers.entrySet().stream()
                        .filter(e -> p.isAnnotationPresent(e.getKey()))
                        .map(e -> {
                            try {
                                return e.getValue().resolve(p, ctx);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        })
                        .findFirst()
                        .orElse(null);
            }

            return method.invoke(controller, args);
        } catch (Exception e) {
            throw new EndpointInvocationException(
                    "Failed to invoke endpoint " + httpMethod + " " + pathPattern.getTemplate(), e);
        }
    }

    public PathPattern getPathPattern() { return pathPattern; }
}