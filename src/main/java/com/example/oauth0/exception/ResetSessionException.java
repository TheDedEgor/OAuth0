package com.example.oauth0.exception;

public class ResetSessionException extends SessionException {
    public ResetSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
