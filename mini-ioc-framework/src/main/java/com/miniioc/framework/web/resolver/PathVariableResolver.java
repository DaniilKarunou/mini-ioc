package com.miniioc.framework.web.resolver;

import com.miniioc.framework.annotation.web.PathVariable;
import com.miniioc.framework.web.RequestContext;
import com.miniioc.framework.util.mapper.TypeMapperRegistry;
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