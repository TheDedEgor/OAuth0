package com.example.telegramauth.controller;

import com.example.telegramauth.exception.ExpiredTimeUuidException;
import com.example.telegramauth.exception.NotFoundSessionException;
import com.example.telegramauth.model.dto.ServiceInfoDTO;
import com.example.telegramauth.service.AuthSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AuthSessionService authSessionService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/init")
    public ResponseEntity<?> init(@RequestParam String uuid) throws NotFoundSessionException, ExpiredTimeUuidException {
        var session = authSessionService.get(uuid);
        return ResponseEntity.ok(new ServiceInfoDTO(session.getExternalServiceConfig()));
    }
}
