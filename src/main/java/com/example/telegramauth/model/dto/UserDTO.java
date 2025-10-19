package com.example.telegramauth.model.dto;

import com.pengrad.telegrambot.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserDTO {
    // Идентификатор сессии
    private String uuid;
    // Данные пользователя
    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    public UserDTO(String uuid, User user) {
        this.uuid = uuid;
        this.id = user.id();
        this.username = user.username();
        this.firstName = user.firstName();
        this.lastName = user.lastName();
    }
}
