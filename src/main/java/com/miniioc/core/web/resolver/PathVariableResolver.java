package com.miniioc.core.web.resolver;

import com.miniioc.core.annotation.request.PathVariable;
import com.miniioc.core.web.RequestContext;
import com.miniioc.util.mapper.TypeMapperRegistry;
import java.lang.reflect.Parameter;

public class PathVariableResolver implements ParameterResolver {

    private final TypeMapperRegistry typeMapper;

    public PathVariableResolver(TypeMapperRegistry typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, RequestContext ctx) throws Exception{
        String name = parameter.getAnnotation(PathVariable.class).value();
        return typeMapper.map(parameter.getType(), ctx.pathVariables().get(name));
    }
}