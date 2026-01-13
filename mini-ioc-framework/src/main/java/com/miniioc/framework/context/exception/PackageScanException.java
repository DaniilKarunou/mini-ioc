package com.miniioc.framework.context.exception;

public class PackageScanException extends Exception {
    public PackageScanException(String message) {
        super(message);
    }

    public PackageScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
