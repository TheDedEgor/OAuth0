package com.example.telegramauth.controller;

import com.example.telegramauth.model.dto.ExternalServiceConfigDTO;
import com.example.telegramauth.service.AuthSessionService;
import com.example.telegramauth.service.QrCodeService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    @Value("${bot.url}")
    private String botUrl;

    private final AuthSessionService authSessionService;

    private final QrCodeService qrCodeService;

    @PostMapping("/create")
    public String create(@Valid @RequestBody ExternalServiceConfigDTO config) {
        return authSessionService.create(config);
    }

    @PostMapping("/generateQrCode")
    public String generateQrCode(@Valid @RequestBody ExternalServiceConfigDTO config) throws IOException, WriterException {
        var id = authSessionService.create(config);
        var text = botUrl + "?start=" + id;
        var qrCode = qrCodeService.generateQrCode(text, 200, 200);
        return Base64.getEncoder().encodeToString(qrCode);
    }
}
