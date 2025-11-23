package com.example.oauth0.model.dto;

import com.pengrad.telegrambot.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramDataDTO {
    private User user;
    private Long authDate;
    private String startParam; // В данном случае хранит в себе uuid сессии
}
