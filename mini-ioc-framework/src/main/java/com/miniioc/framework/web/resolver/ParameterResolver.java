package com.miniioc.framework.web.resolver;

import com.miniioc.framework.web.RequestContext;

import java.lang.reflect.Parameter;

public interface ParameterResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter, RequestContext ctx) throws Exception;
}
