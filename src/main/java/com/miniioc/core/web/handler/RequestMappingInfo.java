package com.miniioc.core.web.handler;

import com.miniioc.core.annotation.http.HttpMethod;

public final class RequestMappingInfo {

    private final HttpMethod httpMethod;
    private final PathPattern pathPattern;

    public RequestMappingInfo(
            HttpMethod httpMethod,
            String path
    ) {
        this.httpMethod = httpMethod;
        this.pathPattern = new PathPattern(path);
    }

    public boolean matches(String method, String path) {
        return httpMethod.name().equalsIgnoreCase(method)
                && pathPattern.matches(path);
    }

    public PathPattern getPathPattern() {
        return pathPattern;
    }
}
