package com.miniioc.framework.web.resolver;

import com.miniioc.framework.annotation.web.HttpMethod;
import com.miniioc.framework.annotation.web.RequestMapping;
import com.miniioc.framework.web.handler.RequestMappingInfo;
import com.miniioc.framework.util.AnnotationExtractor;
import com.miniioc.framework.util.MappingValueResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;


public class RequestMappingResolver {

    public Optional<RequestMappingInfo> resolve(Method method) {
        RequestMapping direct = method.getAnnotation(RequestMapping.class);
        if (direct != null) {
            return Optional.of(toInfo(direct));
        }

        for (Annotation ann : method.getAnnotations()) {
            RequestMapping meta = ann.annotationType().getAnnotation(RequestMapping.class);
            if (meta != null) {
                return resolveMeta(ann, meta);
            }
        }

        return Optional.empty();
    }

    private Optional<RequestMappingInfo> resolveMeta(Annotation ann, RequestMapping meta) {
        String path = MappingValueResolver.firstOrDefault(
                AnnotationExtractor.getStrings(ann, "path"),
                AnnotationExtractor.getStrings(ann, "value"),
                "/"
        );
        HttpMethod method = MappingValueResolver.firstOrDefault(meta.method(), HttpMethod.GET);
        return Optional.of(new RequestMappingInfo(method, path));
    }

    private RequestMappingInfo toInfo(RequestMapping rm) {
        String path = MappingValueResolver.firstOrDefault(rm.path(), rm.value(), "/");
        HttpMethod method = MappingValueResolver.firstOrDefault(rm.method(), HttpMethod.GET);
        return new RequestMappingInfo(method, path);
    }
}