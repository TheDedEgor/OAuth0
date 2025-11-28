package com.example.oauth0.model.dto;

import com.example.oauth0.model.entity.AuthSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AuthSessionDTO {
    private String uuid;
    private ZonedDateTime expiredAt;

    public AuthSessionDTO(AuthSession authSession) {
        this.uuid = authSession.getUuid();
        this.expiredAt = authSession.getExpiredAt();
    }
}
