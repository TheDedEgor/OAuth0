package com.example.telegramauth.model.dto;

import com.example.telegramauth.model.entity.AuthSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AuthSessionDTO {
    private String uuid;
    private LocalDateTime expiredAt;

    public AuthSessionDTO(AuthSession authSession) {
        this.uuid = authSession.getUuid();
        this.expiredAt = authSession.getExpiredAt();
    }
}
