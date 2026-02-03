package com.example.oauth0.service;

import com.example.oauth0.apiClient.ApiClient;
import com.example.oauth0.exception.*;
import com.example.oauth0.model.dto.AuthSessionDTO;
import com.example.oauth0.model.dto.CreateAuthSessionDTO;
import com.example.oauth0.model.dto.ExternalServiceConfigDTO;
import com.example.oauth0.model.dto.UserDTO;
import com.example.oauth0.model.entity.AuthSession;
import com.example.oauth0.model.enums.SessionStatus;
import com.example.oauth0.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final AuthSessionRepository authSessionRepository;

    private final ApiClient apiClient;

    /**
     * Создание новой аутентификационной сессии
     */
    public AuthSessionDTO create(CreateAuthSessionDTO createAuthSessionDTO) {
        var authSession = new AuthSession(createAuthSessionDTO);
        return new AuthSessionDTO(authSessionRepository.save(authSession));
    }

    /**
     * Сброс постоянной сессии к начальным параметрам
     * @param uuid идентификатор сессиии
     */
    @Transactional
    public void reset(String uuid) {
        var session = get(uuid);
        var errorUrl = session.getExternalServiceConfig().getErrorUrl();

        if (!session.getPermanent()) {
            throw new ResetSessionException("Сбросить можно только постоянную сессию", errorUrl, session.getUuid());
        }
        session.setProviderId(null);
        session.setStatus(SessionStatus.ACTIVE);
        authSessionRepository.save(session);
    }

    /**
     * Связывание сессии с конкретным пользователем, ожидание подтверждения входа
     * @param uuid идентификатор сессиии
     * @param providerId идентификатор пользователя из централизованного приложения, через которое осуществляется вход
     */
    @Transactional
    public ExternalServiceConfigDTO link(String uuid, Long providerId) {
        var session = get(uuid);
        var errorUrl = session.getExternalServiceConfig().getErrorUrl();

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new ActiveSessionException("Связать можно только активную сессию", errorUrl, session.getUuid());
        }
        session.setProviderId(providerId);
        // TODO на данном этапе возможна реализация сразу подтвержденной сессии, если сможем определить, что сессия относится к нужному пользователю,
        // например через криптографический стойкий хеш в защищенных куках
        session.setStatus(SessionStatus.AWAITING_CONFIRMATION);
        authSessionRepository.save(session);

        return new ExternalServiceConfigDTO(session.getExternalServiceConfig(), session.getStatus());
    }

    /**
     * Подтверждение/отмена аутентификации пользователем
     * @param uuid идентификатор сессиии
     * @param providerId идентификатор пользователя из централизованного приложения, через которое осуществляется вход
     * @param isConfirmed ответ пользователя по согласию
     */
    @Transactional
    public SessionStatus confirm(String uuid, Long providerId, boolean isConfirmed) {
        var session = get(uuid);
        var errorUrl = session.getExternalServiceConfig().getErrorUrl();

        if (!session.getProviderId().equals(providerId)) {
            throw new UserSessionException("Данная сессия не связана с нужным пользователем", errorUrl, session.getUuid());
        }
        if (session.getStatus() != SessionStatus.AWAITING_CONFIRMATION) {
            throw new AwaitingConfrimationSessionException("Подтвердить можно только ожидающую подтверждения сессию", errorUrl, session.getUuid());
        }
        session.setStatus(isConfirmed ? SessionStatus.CONFIRMED : SessionStatus.REVOKED);
        authSessionRepository.save(session);

        return session.getStatus();
    }

    /**
     * Запрос на аутентификацию пользователя, аутентифицирует только с подтвержденной сессией
     * @param uuid идентификатор сессиии
     * @param providerId идентификатор пользователя из централизованного приложения, через которое осуществляется вход
     */
    @Transactional
    public void auth(String uuid, Long providerId) {
        var session = get(uuid);
        var errorUrl = session.getExternalServiceConfig().getErrorUrl();

        if (!session.getProviderId().equals(providerId)) {
            throw new UserSessionException("Данная сессия не связана с нужным пользователем", errorUrl, session.getUuid());
        }
        if (session.getStatus() != SessionStatus.CONFIRMED) {
            throw new ConfirmedSessionException("Начать аутентификацию можно только в подтвержденной пользователем сессии", errorUrl, session.getUuid());
        }
        var authUrl = session.getExternalServiceConfig().getAuthUrl();
        var response = apiClient.auth(authUrl, new UserDTO(uuid, providerId));
        if (response.getStatusCode().is2xxSuccessful()) {
            var contentType = response.getHeaders().getContentType();
            if (contentType != null) {
                throw new NotFoundSessionException(String.format("Сервер вернул неожиданный Content-Type: %s", contentType), errorUrl, session.getUuid());
            }
            session.setStatus(SessionStatus.FINISHED);
            authSessionRepository.save(session);
        } else {
            throw new SessionException("Ошибка во время аутентификации", errorUrl, session.getUuid());
        }
    }

    private AuthSession get(String uuid) {
        var authSession = authSessionRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Не удалось найти сессию"));

        var errorUrl = authSession.getExternalServiceConfig().getErrorUrl();
        if (authSession.isExpired()) {
            authSession.setStatus(SessionStatus.EXPIRED);
            authSessionRepository.save(authSession);
            throw new ExpiredTimeSessionException("Время действия сессии истекло", errorUrl, authSession.getUuid());
        }

        if (authSession.getStatus() == SessionStatus.FINISHED) {
            throw new FinishedSessionException("Данная сессия уже завершена", errorUrl, authSession.getUuid());
        }

        return authSession;
    }

    @Scheduled(cron = "0 0 * * * *")
    protected void updateExpiredSessions() {
        var sessions = authSessionRepository.findAllByStatusAndPermanentIsFalse(SessionStatus.ACTIVE);
        sessions.forEach(session -> {
            if (session.isExpired()) {
                session.setStatus(SessionStatus.EXPIRED);
            }
        });
        authSessionRepository.saveAll(sessions);
    }
}
