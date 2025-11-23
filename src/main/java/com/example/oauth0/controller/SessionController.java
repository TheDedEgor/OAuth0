package com.example.oauth0.controller;

import com.example.oauth0.exception.ExpiredTimeSessionException;
import com.example.oauth0.exception.NotFoundSessionException;
import com.example.oauth0.model.dto.AuthSessionDTO;
import com.example.oauth0.model.dto.CreateAuthSessionDTO;
import com.example.oauth0.model.entity.AuthSession;
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

    @GetMapping("/{uuid}")
    public AuthSession get(@PathVariable String uuid) throws NotFoundSessionException, ExpiredTimeSessionException {
        return authSessionService.get(uuid);
    }
}
