package com.example.oauth0.model.entity;

import com.example.oauth0.model.dto.CreateAuthSessionDTO;
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

    public ExternalServiceConfig(CreateAuthSessionDTO createAuthSessionDTO) {
        this.authUrl = createAuthSessionDTO.getAuthUrl();
        this.serviceName = createAuthSessionDTO.getServiceName();
        this.description = createAuthSessionDTO.getDescription();
        this.logoUrl = createAuthSessionDTO.getLogoUrl();
    }
}
