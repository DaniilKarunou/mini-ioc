package com.miniioc.framework.web.exception;

import com.miniioc.framework.web.RequestContext;


public interface ExceptionHandler {
    boolean supports(Throwable ex);
    ErrorResponse handle(Throwable ex, RequestContext ctx);
}
