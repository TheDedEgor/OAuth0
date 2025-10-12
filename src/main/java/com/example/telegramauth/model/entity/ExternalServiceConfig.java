package com.example.telegramauth.model.entity;

import com.example.telegramauth.model.dto.ExternalServiceConfigDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "external_service_configs")
@Getter @Setter @NoArgsConstructor
public class ExternalServiceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authUrl;

    private String serviceName;

    private Boolean enableCreationCheck = true;

    public ExternalServiceConfig(ExternalServiceConfigDTO config) {
        this.authUrl = config.getAuthUrl();
        this.serviceName = config.getServiceName();
        this.enableCreationCheck = config.getEnableCreationCheck();
    }
}
