package com.example.telegramauth.controller;

import com.example.telegramauth.exception.ExpiredTimeSessionException;
import com.example.telegramauth.exception.NotFoundSessionException;
import com.example.telegramauth.model.dto.ExternalServiceConfigDTO;
import com.example.telegramauth.service.AuthSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AuthSessionService authSessionService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/init")
    public ResponseEntity<?> init(@RequestParam String uuid) {
        try {
            var session = authSessionService.get(uuid);
            return ResponseEntity.ok(new ExternalServiceConfigDTO(session.getExternalServiceConfig()));
        } catch (NotFoundSessionException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ExpiredTimeSessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
