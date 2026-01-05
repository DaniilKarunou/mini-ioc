package com.miniioc.core.web.resolver;

import com.miniioc.core.annotation.request.RequestBody;
import com.miniioc.core.web.RequestContext;
import com.miniioc.util.json.JsonMapper;
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
