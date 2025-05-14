package com.example.telegramauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ServiceParamsDTO {
    @NotBlank(message = "URL для авторизации обязателен")
    private String authUrl;
    private String serviceName;
    private Boolean enableCreationCheck = true;
}
