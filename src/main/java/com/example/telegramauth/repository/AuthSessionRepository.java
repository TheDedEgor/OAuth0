package com.example.telegramauth.repository;

import com.example.telegramauth.model.entity.AuthSession;
import com.example.telegramauth.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {
    List<AuthSession> findAllByStatusAndPermanentIsFalse(SessionStatus status);
}
