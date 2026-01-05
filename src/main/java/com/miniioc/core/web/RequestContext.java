package com.miniioc.core.web;

import java.util.Map;

public record RequestContext(Map<String, String> pathVariables, Map<String, String> queryParams, String body) {}