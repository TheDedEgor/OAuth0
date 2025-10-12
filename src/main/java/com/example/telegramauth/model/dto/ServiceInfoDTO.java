package com.example.telegramauth.model.dto;

import com.example.telegramauth.model.entity.ExternalServiceConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ServiceInfoDTO {
    private String authUrl;
    private String name;
    private String description;
    private String logoUrl;

    public ServiceInfoDTO(ExternalServiceConfig externalServiceConfig) {
        this.authUrl = externalServiceConfig.getAuthUrl();
        this.name = externalServiceConfig.getServiceName();
    }
}
