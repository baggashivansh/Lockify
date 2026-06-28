package com.lockify.shared.exception;

import org.springframework.http.HttpStatus;

/** JWT invalid/expired/tampered - 401 Unauthorized */
public class TokenException extends LockifyException {

    public TokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
