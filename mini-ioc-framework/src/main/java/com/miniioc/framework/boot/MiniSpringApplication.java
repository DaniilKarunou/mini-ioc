package com.miniioc.framework.boot;

import com.miniioc.framework.beans.ApplicationContext;
import com.miniioc.framework.web.exception.DefaultExceptionHandler;
import com.miniioc.framework.web.exception.ExceptionHandler;
import com.miniioc.framework.web.handler.EndpointHandler;
import com.miniioc.framework.web.registry.EndpointRegistry;
import com.miniioc.framework.web.resolver.*;
import com.miniioc.framework.web.server.WebServer;
import com.miniioc.framework.util.mapper.TypeMapperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


public class MiniSpringApplication {

    private MiniSpringApplication(){}

    private static final Logger logger = LoggerFactory.getLogger(MiniSpringApplication.class);

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