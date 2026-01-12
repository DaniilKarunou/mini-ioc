package com.miniioc.core.web.registry;

import com.miniioc.core.context.beans.ApplicationContext;
import com.miniioc.core.annotation.injection.Controller;
import com.miniioc.core.web.handler.EndpointHandler;
import com.miniioc.core.web.resolver.ParameterResolver;
import com.miniioc.core.web.resolver.RequestMappingResolver;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class EndpointRegistry {

    private final RequestMappingResolver resolver;

    public EndpointRegistry(RequestMappingResolver resolver) {
        this.resolver = resolver;
    }

    public List<EndpointHandler> build(
            ApplicationContext context,
            List<ParameterResolver> resolvers
    ) {
        List<EndpointHandler> handlers = new ArrayList<>();

        for (Class<?> clazz : context.getBeanDefinitions().keySet()) {
            if (!clazz.isAnnotationPresent(Controller.class)) continue;

            Object controller = context.getBean(clazz);

            for (Method method : clazz.getDeclaredMethods()) {
                resolver.resolve(method).ifPresent(mapping ->
                        handlers.add(new EndpointHandler(
                                controller,
                                method,
                                mapping,
                                resolvers
                        ))
                );
            }
        }
        return handlers;
    }
}

