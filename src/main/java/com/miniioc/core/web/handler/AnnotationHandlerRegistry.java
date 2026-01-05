package com.miniioc.core.web.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AnnotationHandlerRegistry {
    private final Map<Class<? extends Annotation>, BiFunction<Object, Method, EndpointHandler>> handlers = new HashMap<>();

    public void register(Class<? extends Annotation> annotation,
                         BiFunction<Object, Method, EndpointHandler> handler) {
        handlers.put(annotation, handler);
    }

    public EndpointHandler handle(Class<? extends Annotation> annotation, Object controller, Method method) {
        return handlers.get(annotation).apply(controller, method);
    }

    public Map<Class<? extends Annotation>, BiFunction<Object, Method, EndpointHandler>> getHandlers() {
        return handlers;
    }
}
