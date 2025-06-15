package com.example.telegramauth.service;

import com.example.telegramauth.exception.ExpiredTimeUuidException;
import com.example.telegramauth.exception.NotFoundSessionException;
import com.example.telegramauth.model.dto.ExternalServiceConfigDTO;
import com.example.telegramauth.model.entity.AuthSession;
import com.example.telegramauth.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final AuthSessionRepository authSessionRepository;

    public String create(ExternalServiceConfigDTO config) {
        var authSession = new AuthSession(config);
        return authSessionRepository.save(authSession).getUuid();
    }

    public AuthSession get(String uuid) throws NotFoundSessionException, ExpiredTimeUuidException {
        return get(uuid, false);
    }

    public AuthSession get(String uuid, Boolean checkTime) throws NotFoundSessionException, ExpiredTimeUuidException {
        var authSession = authSessionRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundSessionException("Данный uuid не найден"));

        if (checkTime) {
            var createdAt = authSession.getCreatedAt();
            var duration = Duration.between(createdAt, LocalDateTime.now());
            if (duration.toMinutes() > 1) {
                throw new ExpiredTimeUuidException("Время действия uuid истекло");
            }
        }

        return authSession;
    }
}
