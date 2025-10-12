package com.example.telegramauth.model.entity;

import com.example.telegramauth.model.dto.ExternalServiceConfigDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_sessions")
@Getter @Setter @NoArgsConstructor
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Fetch(FetchMode.JOIN)
    private ExternalServiceConfig externalServiceConfig;

    public AuthSession(ExternalServiceConfigDTO config) {
        this.externalServiceConfig = new ExternalServiceConfig(config);
    }
}
