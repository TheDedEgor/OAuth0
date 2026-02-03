package com.example.oauth0.exception;

public class ExpiredTimeSessionException extends SessionException {
    public ExpiredTimeSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
