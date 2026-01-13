package com.miniioc.framework.web.resolver;

import com.miniioc.framework.annotation.web.RequestBody;
import com.miniioc.framework.web.RequestContext;
import com.miniioc.framework.util.json.JsonMapper;
import java.lang.reflect.Parameter;

public class RequestBodyResolver implements ParameterResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, RequestContext ctx) throws Exception {
        String body = ctx.body();
        return JsonMapper.fromJson(body, parameter.getType());
    }
}
