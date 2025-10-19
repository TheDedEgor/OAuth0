package com.example.telegramauth.model.entity;

import com.example.telegramauth.model.dto.AuthSessionDTO;
import com.example.telegramauth.model.enums.SessionStatus;
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
@Getter
@Setter
@NoArgsConstructor
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Если null → сессия бессрочная.
     * Если не null → сессия истечёт в указанное время.
     */
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * Признак постоянной (вечной) сессии.
     * true = без срока действия (expiresAt = null)
     */
    @Column(nullable = false)
    private boolean permanent = false;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Fetch(FetchMode.JOIN)
    private ExternalServiceConfig externalServiceConfig;

    public AuthSession(AuthSessionDTO authSessionDTO) {
        this.permanent = authSessionDTO.getPermanent();
        this.externalServiceConfig = new ExternalServiceConfig(authSessionDTO);
        if (!permanent) {
            this.expiredAt = LocalDateTime.now().plusSeconds(authSessionDTO.getLifetimeSeconds());
        }
    }

    public boolean isExpired() {
        return !permanent && expiredAt != null && LocalDateTime.now().isAfter(expiredAt);
    }
}
