package com.example.telegramauth.service;

import com.example.telegramauth.exception.ExpiredTimeSessionException;
import com.example.telegramauth.exception.NotFoundSessionException;
import com.example.telegramauth.model.dto.AuthSessionDTO;
import com.example.telegramauth.model.entity.AuthSession;
import com.example.telegramauth.model.enums.SessionStatus;
import com.example.telegramauth.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final AuthSessionRepository authSessionRepository;

    public String create(AuthSessionDTO authSessionDTO) {
        var authSession = new AuthSession(authSessionDTO);
        return authSessionRepository.save(authSession).getUuid();
    }

    public AuthSession get(String uuid) throws NotFoundSessionException, ExpiredTimeSessionException {
        var authSession = authSessionRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundSessionException("Не удалось найти сессию"));

        if (authSession.isExpired()) {
            authSession.setStatus(SessionStatus.EXPIRED);
            authSessionRepository.save(authSession);
            throw new ExpiredTimeSessionException("Время действия сессии истекло");
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
