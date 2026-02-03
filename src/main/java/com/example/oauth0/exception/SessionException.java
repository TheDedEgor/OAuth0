package com.example.oauth0.exception;

import lombok.Getter;

@Getter
public class SessionException extends RuntimeException {
    private final String errorUrl;
    private final String sessionId;

    public SessionException(String message, String errorUrl, String sessionId) {
        super(message);
        this.errorUrl = errorUrl;
        this.sessionId = sessionId;
    }
}
