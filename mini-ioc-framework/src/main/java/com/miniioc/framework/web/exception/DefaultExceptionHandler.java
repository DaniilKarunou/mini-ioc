package com.miniioc.framework.web.exception;

import com.miniioc.framework.web.RequestContext;

public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public boolean supports(Throwable ex) {
        return true;
    }

    @Override
    public ErrorResponse handle(Throwable ex, RequestContext ctx) {
        if (ex instanceof HttpException he) {
            return new ErrorResponse(
                    he.getStatus(),
                    he.getBody(),
                    he.getClass().getSimpleName(),
                    ctx.path()
            );
        }

        return new ErrorResponse(
                500,
                "Internal Server Error",
                ex.getClass().getSimpleName(),
                ctx.path()
        );
    }
}
