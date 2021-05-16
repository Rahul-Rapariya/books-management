package com.assignment.booksmanagement.exception;

public class RequestNotValidException extends RuntimeException {
    public RequestNotValidException(String message) {
        super(message);
    }

    public RequestNotValidException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
