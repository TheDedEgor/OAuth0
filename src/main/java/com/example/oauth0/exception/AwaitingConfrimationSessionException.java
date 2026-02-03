package com.example.oauth0.exception;

public class AwaitingConfrimationSessionException extends SessionException {
    public AwaitingConfrimationSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
