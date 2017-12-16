package com.devotion.capture;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = -3685317928211708951L;

    public ValidationException(String message) {
        super(message);
    }
}
