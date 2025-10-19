package com.example.telegramauth.model.entity;

import com.example.telegramauth.model.dto.AuthSessionDTO;
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

    @Column(nullable = false)
    private String serviceName;

    private String description;

    private String logoUrl;

    public ExternalServiceConfig(AuthSessionDTO authSessionDTO) {
        this.authUrl = authSessionDTO.getAuthUrl();
        this.serviceName = authSessionDTO.getServiceName();
        this.description = authSessionDTO.getDescription();
        this.logoUrl = authSessionDTO.getLogoUrl();
    }
}
