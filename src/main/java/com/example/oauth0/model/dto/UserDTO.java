package com.example.oauth0.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserDTO {
    // Идентификатор сессии
    private String uuid;
    // Данные пользователя
    private Long id;

    public UserDTO(String uuid, Long providerId) {
        this.uuid = uuid;
        this.id = providerId;
    }
}
