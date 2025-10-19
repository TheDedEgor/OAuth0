package com.example.telegramauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class AuthSessionDTO {
    @URL(message = "Должен быть корректный URL")
    @NotBlank(message = "URL обязательно должен быть указан")
    private String authUrl;
    @NotBlank(message = "Название сервиса обязательно")
    private String serviceName;
    private String description;
    private String logoUrl;
    private Boolean permanent = false;
    private Long lifetimeSeconds = 300L;
}
