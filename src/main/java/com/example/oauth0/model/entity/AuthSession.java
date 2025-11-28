package com.example.oauth0.model.entity;

import com.example.oauth0.model.dto.CreateAuthSessionDTO;
import com.example.oauth0.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.ZonedDateTime;

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
    private ZonedDateTime createdAt;

    /**
     * Если null → сессия бессрочная.
     * Если не null → сессия истечёт в указанное время.
     */
    private ZonedDateTime expiredAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * Признак постоянной (вечной) сессии.
     * true = без срока действия (expiredAt = null)
     */
    @Column(nullable = false)
    private boolean permanent = false;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Fetch(FetchMode.JOIN)
    private ExternalServiceConfig externalServiceConfig;

    public AuthSession(CreateAuthSessionDTO createAuthSessionDTO) {
        this.permanent = createAuthSessionDTO.getPermanent();
        this.externalServiceConfig = new ExternalServiceConfig(createAuthSessionDTO);
        if (!permanent) {
            this.expiredAt = ZonedDateTime.now().plusSeconds(createAuthSessionDTO.getLifetimeSeconds());
        }
    }

    public boolean isExpired() {
        return !permanent && expiredAt != null && ZonedDateTime.now().isAfter(expiredAt);
    }
}
