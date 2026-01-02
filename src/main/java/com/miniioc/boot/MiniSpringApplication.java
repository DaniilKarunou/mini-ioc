package com.miniioc.boot;

import com.miniioc.core.annotation.Get;
import com.miniioc.core.annotation.Post;
import com.miniioc.core.context.ApplicationContext;
import com.miniioc.core.web.AnnotationHandlerRegistry;
import com.miniioc.core.web.EndpointHandler;
import com.miniioc.core.web.EndpointRegistry;
import com.miniioc.core.web.WebServer;
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
        annotationRegistry.register(Get.class, (controller, method) ->
                new EndpointHandler(controller, method, "GET", method.getAnnotation(Get.class).value()));
        annotationRegistry.register(Post.class, (controller, method) ->
                new EndpointHandler(controller, method, "POST", method.getAnnotation(Post.class).value()));

        EndpointRegistry endpointRegistry = new EndpointRegistry(context, annotationRegistry);
        Map<String, EndpointHandler> urlMappings = endpointRegistry.buildMappings();

        WebServer server = new WebServer(urlMappings);
        server.start(8080);

        logger.info("MiniSpringApplication started on port 8080");
    }
}