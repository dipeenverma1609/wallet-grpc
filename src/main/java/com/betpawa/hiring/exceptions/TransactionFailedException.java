package com.betpawa.hiring.exceptions;

public class TransactionFailedException extends Exception {
    private String message;
    private Throwable cause;

    public TransactionFailedException() {
        super();
    }

    public TransactionFailedException(String message) {
        super(message);
        this.message = message;
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public TransactionFailedException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }
}
