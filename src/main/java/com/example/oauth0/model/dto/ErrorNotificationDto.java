package com.example.oauth0.model.dto;

import com.example.oauth0.exception.SessionException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter @NoArgsConstructor
public class ErrorNotificationDto {
    private String sessionId;
    private String type;
    private String message;
    private ZonedDateTime timestamp;

    public ErrorNotificationDto(SessionException ex) {
        this.sessionId = ex.getSessionId();
        this.type = ex.getClass().getName();
        this.message = ex.getMessage();
        this.timestamp = ZonedDateTime.now();
    }
}
