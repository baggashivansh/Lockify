package com.lockify.phase4.enterprise.ratelimit.exception;

import com.lockify.shared.exception.LockifyException;
import org.springframework.http.HttpStatus;

/** Redis sliding window limit exceed hone pe throw hota hai */
public class RateLimitExceededException extends LockifyException {

    public RateLimitExceededException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
