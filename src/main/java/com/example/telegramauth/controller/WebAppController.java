package com.example.telegramauth.controller;

import com.example.telegramauth.apiClient.AuthApiClient;
import com.example.telegramauth.exception.ExpiredTimeSessionException;
import com.example.telegramauth.exception.NotFoundSessionException;
import com.example.telegramauth.model.dto.ExternalServiceConfigDTO;
import com.example.telegramauth.model.dto.TelegramDataDTO;
import com.example.telegramauth.model.dto.UserDTO;
import com.example.telegramauth.security.TelegramInitData;
import com.example.telegramauth.service.AuthSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class WebAppController {

    private final AuthApiClient authApiClient;

    private final AuthSessionService authSessionService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/init")
    public ResponseEntity<?> init(@TelegramInitData TelegramDataDTO tgData) {
        try {
            var session = authSessionService.get(tgData.getStartParam());
            return ResponseEntity.ok(new ExternalServiceConfigDTO(session.getExternalServiceConfig()));
        } catch (NotFoundSessionException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ExpiredTimeSessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/api/auth")
    public ResponseEntity<?> auth(@TelegramInitData TelegramDataDTO tgData) {
        try {
            var session = authSessionService.get(tgData.getStartParam());
            var serviceConfig = session.getExternalServiceConfig();
            authApiClient.auth(serviceConfig.getAuthUrl(), new UserDTO(session.getUuid(), tgData.getUser()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundSessionException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ExpiredTimeSessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
