package com.example.oauth0.exception;

public class FinishedSessionException extends SessionException {
    public FinishedSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
