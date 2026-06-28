package com.lockify.shared.exception;

import org.springframework.http.HttpStatus;

/** Duplicate email/username pe throw hota hai - 409 Conflict */
public class DuplicateResourceException extends LockifyException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
