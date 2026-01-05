package com.miniioc.boot;

import com.miniioc.core.annotation.http.*;
import com.miniioc.core.annotation.request.PathVariable;
import com.miniioc.core.annotation.request.RequestBody;
import com.miniioc.core.annotation.request.RequestParam;
import com.miniioc.core.context.beans.ApplicationContext;
import com.miniioc.core.web.exception.DefaultExceptionHandler;
import com.miniioc.core.web.exception.ExceptionHandler;
import com.miniioc.core.web.handler.*;
import com.miniioc.core.web.registry.EndpointRegistry;
import com.miniioc.core.web.resolver.ParameterResolver;
import com.miniioc.core.web.resolver.PathVariableResolver;
import com.miniioc.core.web.resolver.RequestBodyResolver;
import com.miniioc.core.web.resolver.RequestParamResolver;
import com.miniioc.core.web.server.WebServer;
import com.miniioc.util.mapper.TypeMapperRegistry;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class MiniSpringApplication {

    private MiniSpringApplication(){}

    private static final Logger logger = Logger.getLogger(MiniSpringApplication.class.getName());

    public static void run(Class<?> appClass) throws Exception {
        ApplicationContext context = new ApplicationContext();

        context.registerBean(appClass, true);
        context.scanPackage(appClass.getPackageName());
        context.initializeSingletons();

        AnnotationHandlerRegistry annotationRegistry = new AnnotationHandlerRegistry();
        registerDefaultHandlers(annotationRegistry);

        EndpointRegistry endpointRegistry = new EndpointRegistry(context, annotationRegistry);
        List<EndpointHandler> urlMappings = endpointRegistry.buildMappings();

        List<ExceptionHandler> exceptionHandlers = List.of(
                new DefaultExceptionHandler()
        );

        WebServer server = new WebServer(urlMappings, exceptionHandlers);
        server.start(8080);

        logger.info("MiniSpringApplication started on port 8080");
    }

    private static void registerDefaultHandlers(AnnotationHandlerRegistry registry) {
        TypeMapperRegistry typeMapperRegistry = new TypeMapperRegistry();
        Map<Class<? extends Annotation>, ParameterResolver> parameterResolvers = Map.of(
                PathVariable.class, new PathVariableResolver(typeMapperRegistry),
                RequestParam.class, new RequestParamResolver(typeMapperRegistry),
                RequestBody.class, new RequestBodyResolver()
        );

        registry.register(Get.class, (controller, method) ->
                new EndpointHandler(controller, method, "GET", method.getAnnotation(Get.class).value(), parameterResolvers));

        registry.register(Post.class, (controller, method) ->
                new EndpointHandler(controller, method, "POST", method.getAnnotation(Post.class).value(), parameterResolvers));

        registry.register(Put.class, (controller, method) ->
                new EndpointHandler(controller, method, "PUT", method.getAnnotation(Put.class).value(), parameterResolvers));

        registry.register(Delete.class, (controller, method) ->
                new EndpointHandler(controller, method, "DELETE", method.getAnnotation(Delete.class).value(), parameterResolvers));

        registry.register(Patch.class, (controller, method) ->
                new EndpointHandler(controller, method, "PATCH", method.getAnnotation(Patch.class).value(), parameterResolvers));
    }
}