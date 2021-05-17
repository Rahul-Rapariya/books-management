package com.assignment.booksmanagement.exception;

public class DefaultRuntimeException extends RuntimeException {
    public DefaultRuntimeException(String message) {
        super(message);
    }

    public DefaultRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}