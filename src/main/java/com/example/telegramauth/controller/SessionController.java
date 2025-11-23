package com.example.telegramauth.controller;

import com.example.telegramauth.exception.ExpiredTimeSessionException;
import com.example.telegramauth.exception.NotFoundSessionException;
import com.example.telegramauth.model.dto.AuthSessionDTO;
import com.example.telegramauth.model.dto.CreateAuthSessionDTO;
import com.example.telegramauth.model.entity.AuthSession;
import com.example.telegramauth.service.AuthSessionService;
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

    @GetMapping("/{uuid}")
    public AuthSession get(@PathVariable String uuid) throws NotFoundSessionException, ExpiredTimeSessionException {
        return authSessionService.get(uuid);
    }
}
