package com.miniioc.core.web.resolver;

import com.miniioc.core.web.RequestContext;

import java.lang.reflect.Parameter;

public interface ParameterResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter, RequestContext ctx) throws Exception;
}
