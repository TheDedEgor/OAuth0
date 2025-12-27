package com.example.oauth0.model.dto;

import com.example.oauth0.model.entity.ExternalServiceConfig;
import com.example.oauth0.model.enums.SessionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ExternalServiceConfigDTO {
    private String name;
    private String description;
    private String logoUrl;
    private SessionStatus sessionStatus;

    public ExternalServiceConfigDTO(ExternalServiceConfig externalServiceConfig, SessionStatus status) {
        this.name = externalServiceConfig.getServiceName();
        this.description = externalServiceConfig.getDescription();
        this.logoUrl = externalServiceConfig.getLogoUrl();
        this.sessionStatus = status;
    }
}
