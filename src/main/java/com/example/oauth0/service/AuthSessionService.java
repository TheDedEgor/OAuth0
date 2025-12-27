package com.example.oauth0.service;

import com.example.oauth0.apiClient.AuthApiClient;
import com.example.oauth0.exception.ExpiredTimeSessionException;
import com.example.oauth0.exception.NotFoundSessionException;
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

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final AuthSessionRepository authSessionRepository;

    private final AuthApiClient authApiClient;

    /**
     * Создание новой аутентификационной сессии
     */
    public AuthSessionDTO create(CreateAuthSessionDTO createAuthSessionDTO) {
        var authSession = new AuthSession(createAuthSessionDTO);
        return new AuthSessionDTO(authSessionRepository.save(authSession));
    }

    /**
     * Связывание сессии с конкретным пользователем, ожидание подтверждения входа
     * @param uuid - идентификатор сессиии
     * @param providerId - идентификатор пользователя из централизованного приложения, через которое осуществляется вход
     */
    public ExternalServiceConfigDTO link(String uuid, Long providerId) throws NotFoundSessionException, ExpiredTimeSessionException {
        var session = get(uuid);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new RuntimeException("Связать можно только активную сессию");
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
     * @param uuid - идентификатор сессиии
     * @param providerId - идентификатор пользователя из централизованного приложения, через которое осуществляется вход
     * @param isConfirmed - ответ пользователя по согласию
     */
    public SessionStatus confirm(String uuid, Long providerId, boolean isConfirmed) throws NotFoundSessionException, ExpiredTimeSessionException {
        var session = get(uuid);
        if (!session.getProviderId().equals(providerId)) {
            throw new RuntimeException("Данная сессия не связана с нужным пользователем");
        }
        if (session.getStatus() != SessionStatus.AWAITING_CONFIRMATION) {
            throw new RuntimeException("Подтвердить можно только ожидающую подтверждения сессию");
        }
        session.setStatus(isConfirmed ? SessionStatus.CONFIRMED : SessionStatus.REVOKED);
        authSessionRepository.save(session);

        return session.getStatus();
    }

    /**
     * Запрос на аутентификацию пользователя, аутентифицирует только с подтвержденной сессией
     * @param uuid - идентификатор сессиии
     * @param providerId - идентификатор пользователя из централизованного приложения, через которое осуществляется вход
     */
    public void auth(String uuid, Long providerId) throws NotFoundSessionException, ExpiredTimeSessionException {
        var session = get(uuid);
        if (!session.getProviderId().equals(providerId)) {
            throw new RuntimeException("Данная сессия не связана с нужным пользователем");
        }
        if (session.getStatus() != SessionStatus.CONFIRMED) {
            throw new RuntimeException("Начать аутентификацию можно только в подтвержденной пользователем сессии");
        }
        var authUrl = session.getExternalServiceConfig().getAuthUrl();
        var response = authApiClient.auth(authUrl, new UserDTO(uuid, providerId));
        if (response.getStatusCode().is2xxSuccessful()) {
            var contentType = response.getHeaders().getContentType();
            if (contentType != null) {
                throw new RuntimeException(String.format("Сервер вернул неожиданный Content-Type: %s", contentType));
            }
            session.setStatus(SessionStatus.FINISHED);
            authSessionRepository.save(session);
        } else {
            throw new RuntimeException("Ошибка во время аутентификации");
        }
    }

    private AuthSession get(String uuid) throws NotFoundSessionException, ExpiredTimeSessionException {
        var authSession = authSessionRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundSessionException("Не удалось найти сессию"));

        if (authSession.isExpired()) {
            authSession.setStatus(SessionStatus.EXPIRED);
            authSessionRepository.save(authSession);
            throw new ExpiredTimeSessionException("Время действия сессии истекло");
        }

        if (authSession.getStatus() == SessionStatus.FINISHED) {
            throw new RuntimeException("Данная сессия уже завершена");
        }

        return authSession;
    }

    @Scheduled(cron = "0 0 * * * *")
    private void updateExpiredSessions() {
        var sessions = authSessionRepository.findAllByStatusAndPermanentIsFalse(SessionStatus.ACTIVE);
        sessions.forEach(session -> {
            if (session.isExpired()) {
                session.setStatus(SessionStatus.EXPIRED);
            }
        });
        authSessionRepository.saveAll(sessions);
    }
}
