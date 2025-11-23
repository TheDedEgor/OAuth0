package com.example.oauth0.model.dto;

import com.example.oauth0.model.entity.ExternalServiceConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ExternalServiceConfigDTO {
    private String authUrl;
    private String name;
    private String description;
    private String logoUrl;

    public ExternalServiceConfigDTO(ExternalServiceConfig externalServiceConfig) {
        this.authUrl = externalServiceConfig.getAuthUrl();
        this.name = externalServiceConfig.getServiceName();
        this.description = externalServiceConfig.getDescription();
        this.logoUrl = externalServiceConfig.getLogoUrl();
    }
}
