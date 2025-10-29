package com.example.telegramauth.controller;

import com.example.telegramauth.model.dto.AuthSessionDTO;
import com.example.telegramauth.service.AuthSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final AuthSessionService authSessionService;

    @PostMapping("/create")
    public String create(@Valid @RequestBody AuthSessionDTO authSessionDTO) {
        return authSessionService.create(authSessionDTO);
    }
}
