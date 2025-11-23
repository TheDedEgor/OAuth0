package com.example.oauth0.service;

import com.example.oauth0.exception.ExpiredTimeSessionException;
import com.example.oauth0.exception.NotFoundSessionException;
import com.example.oauth0.model.dto.AuthSessionDTO;
import com.example.oauth0.model.dto.CreateAuthSessionDTO;
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

    public AuthSessionDTO create(CreateAuthSessionDTO createAuthSessionDTO) {
        var authSession = new AuthSession(createAuthSessionDTO);
        return new AuthSessionDTO(authSessionRepository.save(authSession));
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
