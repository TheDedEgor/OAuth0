package com.example.oauth0.controller;

import com.example.oauth0.exception.ExpiredTimeSessionException;
import com.example.oauth0.exception.NotFoundSessionException;
import com.example.oauth0.model.dto.AuthSessionDTO;
import com.example.oauth0.model.dto.CreateAuthSessionDTO;
import com.example.oauth0.model.dto.ExternalServiceConfigDTO;
import com.example.oauth0.model.enums.SessionStatus;
import com.example.oauth0.service.AuthSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final AuthSessionService authSessionService;

    @PostMapping
    public AuthSessionDTO create(@Valid @RequestBody CreateAuthSessionDTO createAuthSessionDTO) {
        return authSessionService.create(createAuthSessionDTO);
    }

    @PostMapping("/link")
    public ExternalServiceConfigDTO link(@RequestParam String uuid, @RequestParam Long providerId) throws NotFoundSessionException, ExpiredTimeSessionException {
        return authSessionService.link(uuid, providerId);
    }

    @PostMapping("/confirm")
    public SessionStatus confirm(@RequestParam String uuid, @RequestParam Long providerId, @RequestParam Boolean isConfirmed) throws NotFoundSessionException, ExpiredTimeSessionException {
        return authSessionService.confirm(uuid, providerId, isConfirmed);
    }

    @PostMapping("/auth")
    public void confirm(@RequestParam String uuid, @RequestParam Long providerId) throws NotFoundSessionException, ExpiredTimeSessionException {
        authSessionService.auth(uuid, providerId);
    }
}
