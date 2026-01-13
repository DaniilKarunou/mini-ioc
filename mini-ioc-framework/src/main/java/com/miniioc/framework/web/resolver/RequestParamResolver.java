package com.miniioc.framework.web.resolver;

import com.miniioc.framework.annotation.web.RequestParam;
import com.miniioc.framework.web.RequestContext;
import com.miniioc.framework.util.mapper.TypeMapperRegistry;
import java.lang.reflect.Parameter;

public class RequestParamResolver implements ParameterResolver {

    private final TypeMapperRegistry typeMapper;

    public RequestParamResolver(TypeMapperRegistry typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, RequestContext ctx) throws Exception {
        String name = parameter.getAnnotation(RequestParam.class).value();
        String value = ctx.queryParams().get(name);
        return typeMapper.map(parameter.getType(), value);
    }
}
