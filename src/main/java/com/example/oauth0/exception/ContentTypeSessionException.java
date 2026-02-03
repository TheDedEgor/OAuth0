package com.example.oauth0.exception;

public class ContentTypeSessionException extends SessionException {
    public ContentTypeSessionException(String message, String errorUrl, String sessionId) {
        super(message, errorUrl, sessionId);
    }
}
