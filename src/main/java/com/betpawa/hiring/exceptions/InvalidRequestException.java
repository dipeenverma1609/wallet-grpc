package com.betpawa.hiring.exceptions;

public class InvalidRequestException extends RuntimeException {
    private String message;
    private Throwable cause;

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }
}
