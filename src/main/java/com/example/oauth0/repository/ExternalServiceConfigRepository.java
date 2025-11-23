package com.example.oauth0.repository;

import com.example.oauth0.model.entity.ExternalServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalServiceConfigRepository extends JpaRepository<ExternalServiceConfig, Long> {
}
