package com.example.telegramauth.model.entity;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_params")
@Getter @Setter @NoArgsConstructor
public class ServiceParams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authUrl;

    private String serviceName;

    public ServiceParams(ServiceParamsDTO serviceParamsDTO) {
        this.authUrl = serviceParamsDTO.getAuthUrl();
        this.serviceName = serviceParamsDTO.getServiceName();
    }
}
