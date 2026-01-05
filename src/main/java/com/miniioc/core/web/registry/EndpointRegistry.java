package com.miniioc.core.web.registry;

import com.miniioc.core.context.beans.ApplicationContext;
import com.miniioc.core.annotation.injection.Controller;
import com.miniioc.core.web.handler.AnnotationHandlerRegistry;
import com.miniioc.core.web.handler.EndpointHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class EndpointRegistry {
    private final ApplicationContext context;
    private final AnnotationHandlerRegistry annotationRegistry;

    public EndpointRegistry(ApplicationContext context, AnnotationHandlerRegistry annotationRegistry) {
        this.context = context;
        this.annotationRegistry = annotationRegistry;
    }

    public List<EndpointHandler> buildMappings() {
        List<EndpointHandler> handlers = new ArrayList<>();

        for (Class<?> clazz : context.getBeanDefinitions().keySet()) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                Object controller = context.getBean(clazz);
                for (Method method : clazz.getDeclaredMethods()) {
                    for (Class<? extends Annotation> annotationClass : annotationRegistry.getHandlers().keySet()) {
                        if (method.isAnnotationPresent(annotationClass)) {
                            EndpointHandler handler = annotationRegistry.handle(annotationClass, controller, method);
                            handlers.add(handler);
                        }
                    }
                }
            }
        }
        return handlers;
    }
}
