package com.lockify.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Base runtime exception - sab custom exceptions isse extend karte hain.
 */
public class LockifyException extends RuntimeException {

    private final HttpStatus status;

    public LockifyException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
