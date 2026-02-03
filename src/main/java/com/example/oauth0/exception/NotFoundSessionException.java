package com.example.oauth0.exception;

public class NotFoundSessionException extends SessionException {
    public NotFoundSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
