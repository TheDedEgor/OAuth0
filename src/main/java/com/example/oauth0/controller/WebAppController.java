package com.example.oauth0.controller;

import com.example.oauth0.exception.ExpiredTimeSessionException;
import com.example.oauth0.exception.NotFoundSessionException;
import com.example.oauth0.model.dto.TelegramDataDTO;
import com.example.oauth0.security.TelegramInitData;
import com.example.oauth0.service.AuthSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class WebAppController {

    private final AuthSessionService authSessionService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/webapp/link")
    public ResponseEntity<?> link(@TelegramInitData TelegramDataDTO tgData, @CookieValue(name = "OAUTH_SECURE_TOKEN", required = false) String secureToken) {
        try {
            System.out.println(secureToken);
            var uuid = tgData.getStartParam();
            var providerId = tgData.getUser().id();
            var serviceInfo = authSessionService.link(uuid, providerId);
            return ResponseEntity.ok(serviceInfo);
        } catch (NotFoundSessionException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ExpiredTimeSessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/api/webapp/confirm")
    public ResponseEntity<?> confirm(@TelegramInitData TelegramDataDTO tgData, @RequestParam Boolean isConfirmed) {
        try {
            var uuid = tgData.getStartParam();
            var providerId = tgData.getUser().id();
            var sessionStatus = authSessionService.confirm(uuid, providerId, isConfirmed);
            return ResponseEntity.ok(sessionStatus);
        } catch (NotFoundSessionException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ExpiredTimeSessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/api/webapp/auth")
    public ResponseEntity<?> auth(@TelegramInitData TelegramDataDTO tgData) {
        try {
            var uuid = tgData.getStartParam();
            var providerId = tgData.getUser().id();
            authSessionService.auth(uuid, providerId);
            return ResponseEntity.ok().build();
        } catch (NotFoundSessionException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ExpiredTimeSessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
