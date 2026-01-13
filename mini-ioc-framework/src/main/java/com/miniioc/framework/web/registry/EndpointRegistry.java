package com.miniioc.framework.web.registry;

import com.miniioc.framework.beans.ApplicationContext;
import com.miniioc.framework.annotation.stereotype.Controller;
import com.miniioc.framework.beans.BeanDefinition;
import com.miniioc.framework.web.handler.EndpointHandler;
import com.miniioc.framework.web.resolver.ParameterResolver;
import com.miniioc.framework.web.resolver.RequestMappingResolver;
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

        for (BeanDefinition bd : context.getBeanDefinitions().values()) {
            Class<?> clazz = bd.getBeanClass();

            if (!clazz.isAnnotationPresent(Controller.class)) continue;

            Object controller = context.getBean(bd.getName());

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

