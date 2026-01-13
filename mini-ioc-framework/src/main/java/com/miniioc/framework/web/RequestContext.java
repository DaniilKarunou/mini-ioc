package com.miniioc.framework.web;

import java.util.Map;

public record RequestContext(Map<String, String> pathVariables, Map<String, String> queryParams, String body) {}