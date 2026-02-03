package com.example.oauth0.exception;

public class ActiveSessionException extends SessionException {
    public ActiveSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
