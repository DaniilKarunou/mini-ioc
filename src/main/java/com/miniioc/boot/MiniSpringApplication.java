package com.miniioc.boot;

import com.miniioc.core.context.beans.ApplicationContext;
import com.miniioc.core.web.exception.DefaultExceptionHandler;
import com.miniioc.core.web.exception.ExceptionHandler;
import com.miniioc.core.web.handler.*;
import com.miniioc.core.web.registry.EndpointRegistry;
import com.miniioc.core.web.resolver.*;
import com.miniioc.core.web.server.WebServer;
import com.miniioc.util.mapper.TypeMapperRegistry;
import java.util.List;
import java.util.logging.Logger;


public class MiniSpringApplication {

    private MiniSpringApplication(){}

    private static final Logger logger = Logger.getLogger(MiniSpringApplication.class.getName());

    public static void run(Class<?> appClass) throws Exception {
        ApplicationContext context = new ApplicationContext();

        context.registerBean(appClass, true);
        context.scanPackage(appClass.getPackageName());
        context.initializeSingletons();

        RequestMappingResolver mappingResolver = new RequestMappingResolver();
        EndpointRegistry endpointRegistry = new EndpointRegistry(mappingResolver);
        TypeMapperRegistry typeMapperRegistry = new TypeMapperRegistry();
        List<ParameterResolver> parameterResolvers = List.of(
                new PathVariableResolver(typeMapperRegistry),
                new RequestParamResolver(typeMapperRegistry),
                new RequestBodyResolver()
        );
        List<EndpointHandler> urlMappings = endpointRegistry.build(context, parameterResolvers);

        List<ExceptionHandler> exceptionHandlers = List.of(
                new DefaultExceptionHandler()
        );

        WebServer server = new WebServer(urlMappings, exceptionHandlers);
        server.start(8080);

        logger.info("MiniSpringApplication started on port 8080");
    }
}