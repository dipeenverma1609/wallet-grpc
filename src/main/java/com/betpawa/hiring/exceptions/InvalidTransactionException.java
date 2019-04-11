package com.betpawa.hiring.exceptions;

public class InvalidTransactionException extends RuntimeException {

    private String message;
    private Throwable cause;

    public InvalidTransactionException() {
        super();
    }

    public InvalidTransactionException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public InvalidTransactionException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }
}
