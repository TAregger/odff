package io.aregger.odff.service;

public class TracefileServiceException extends RuntimeException {

    public TracefileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TracefileServiceException(Throwable cause) {
        super(cause);
    }

}
