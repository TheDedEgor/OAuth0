package com.example.telegramauth.model.entity;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_params")
@Getter @Setter @NoArgsConstructor
public class ServiceParams {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(nullable = false)
    private String authUrl;

    private String serviceName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Boolean enableCreationCheck;

    public ServiceParams(ServiceParamsDTO serviceParamsDTO) {
        this.authUrl = serviceParamsDTO.getAuthUrl();
        this.serviceName = serviceParamsDTO.getServiceName();
        this.enableCreationCheck = serviceParamsDTO.getEnableCreationCheck();
    }
}
