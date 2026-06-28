package com.lockify.shared.exception;

import org.springframework.http.HttpStatus;

/** Invalid credentials - security me exact reason mat batao (user exist karta hai ya nahi) */
public class AuthenticationException extends LockifyException {

    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
