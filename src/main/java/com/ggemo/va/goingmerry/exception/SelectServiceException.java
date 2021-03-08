package com.ggemo.va.goingmerry.exception;

public class SelectServiceException extends RuntimeException {
    public SelectServiceException() {
    }

    public SelectServiceException(String message) {
        super(message);
    }

    public SelectServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectServiceException(Throwable cause) {
        super(cause);
    }

    public SelectServiceException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
