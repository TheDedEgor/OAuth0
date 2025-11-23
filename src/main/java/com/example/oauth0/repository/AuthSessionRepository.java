package com.example.oauth0.repository;

import com.example.oauth0.model.entity.AuthSession;
import com.example.oauth0.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {
    List<AuthSession> findAllByStatusAndPermanentIsFalse(SessionStatus status);
}
