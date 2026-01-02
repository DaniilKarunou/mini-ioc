package com.miniioc.core.web;

import com.miniioc.core.context.ApplicationContext;
import com.miniioc.core.annotation.Controller;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EndpointRegistry {
    private final ApplicationContext context;
    private final AnnotationHandlerRegistry annotationRegistry;

    public EndpointRegistry(ApplicationContext context, AnnotationHandlerRegistry annotationRegistry) {
        this.context = context;
        this.annotationRegistry = annotationRegistry;
    }

    public Map<String, EndpointHandler> buildMappings() throws Exception {
        Map<String, EndpointHandler> map = new HashMap<>();

        for (Class<?> clazz : context.getBeanDefinitions().keySet()) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                Object controller = context.getBean(clazz);
                for (Method method : clazz.getDeclaredMethods()) {
                    for (Class<? extends java.lang.annotation.Annotation> annotationClass : annotationRegistry.getHandlers().keySet()) {
                        if (method.isAnnotationPresent(annotationClass)) {
                            EndpointHandler handler = annotationRegistry.handle(annotationClass, controller, method);
                            map.put(handler.getHttpMethod() + " " + handler.getPath(), handler);
                        }
                    }
                }
            }
        }
        return map;
    }
}
