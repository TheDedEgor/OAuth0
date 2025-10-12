package com.example.telegramauth.repository;

import com.example.telegramauth.model.entity.ExternalServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalServiceConfigRepository extends JpaRepository<ExternalServiceConfig, Long> {
}
