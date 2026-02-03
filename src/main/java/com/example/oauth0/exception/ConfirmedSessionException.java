package com.example.oauth0.exception;

public class ConfirmedSessionException extends SessionException {
    public ConfirmedSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
