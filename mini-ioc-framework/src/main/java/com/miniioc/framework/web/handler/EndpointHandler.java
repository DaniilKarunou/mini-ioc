package com.miniioc.framework.web.handler;

import com.miniioc.framework.web.RequestContext;
import com.miniioc.framework.web.resolver.ParameterResolver;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class EndpointHandler {

    private final Object controller;
    private final Method method;
    private final RequestMappingInfo mapping;
    private final List<ParameterResolver> resolvers;

    public EndpointHandler(
            Object controller,
            Method method,
            RequestMappingInfo mapping,
            List<ParameterResolver> resolvers
    ) {
        this.controller = controller;
        this.method = method;
        this.mapping = mapping;
        this.resolvers = resolvers;
    }

    public boolean matches(String httpMethod, String path) {
        return mapping.matches(httpMethod, path);
    }

    public Object invoke(RequestContext ctx) throws Exception {
        Object[] args = resolveArguments(ctx);
        return method.invoke(controller, args);
    }

    private Object[] resolveArguments(RequestContext ctx) throws Exception {
        Object[] args = new Object[method.getParameterCount()];
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];

            args[i] = resolvers.stream()
                    .filter(r -> r.supports(p))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException("No resolver for " + p))
                    .resolve(p, ctx);
        }
        return args;
    }

    public Map<String, String> extractPathVariables(String path) {
        return mapping.getPathPattern().extract(path);
    }
}