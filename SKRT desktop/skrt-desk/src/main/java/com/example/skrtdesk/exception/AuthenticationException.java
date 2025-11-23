package com.example.skrtdesk.exception;

public class AuthenticationException extends ApiException {
    public AuthenticationException(String message) {
        super(message, 401);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, 401, cause);
    }
}

