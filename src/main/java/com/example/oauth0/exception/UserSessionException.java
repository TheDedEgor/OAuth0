package com.example.oauth0.exception;

public class UserSessionException extends SessionException {
    public UserSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
