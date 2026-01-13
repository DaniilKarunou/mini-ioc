package com.miniioc.framework.web;

import java.util.Map;

public record RequestContext(
        String method,
        String path,
        Map<String, String> pathVariables,
        Map<String, String> queryParams,
        Map<String, String> headers,
        String body
) {}
