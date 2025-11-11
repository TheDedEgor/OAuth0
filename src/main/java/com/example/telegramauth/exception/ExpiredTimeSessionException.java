package com.example.telegramauth.exception;

public class ExpiredTimeSessionException extends Exception {
    public ExpiredTimeSessionException(String message) {
        super(message);
    }
}
