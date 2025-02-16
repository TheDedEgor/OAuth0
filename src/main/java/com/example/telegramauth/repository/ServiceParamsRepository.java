package com.example.telegramauth.repository;

import com.example.telegramauth.model.entity.ServiceParams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceParamsRepository extends JpaRepository<ServiceParams, String> {
    Optional<ServiceParams> findByAuthUrl(String authUrl);
}
