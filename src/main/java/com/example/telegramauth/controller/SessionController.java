package com.example.telegramauth.controller;

import com.example.telegramauth.model.dto.AuthSessionDTO;
import com.example.telegramauth.service.AuthSessionService;
import com.example.telegramauth.service.QrCodeService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    @Value("${bot.url}")
    private String botUrl;

    @Value("${bot.web-app-url}")
    private String webAppUrl;

    private final AuthSessionService authSessionService;

    private final QrCodeService qrCodeService;

    @PostMapping("/create")
    public String create(@Valid @RequestBody AuthSessionDTO authSessionDTO,
                         @RequestParam(required = false, defaultValue = "false") boolean qr) throws IOException, WriterException {
        var uuid = authSessionService.create(authSessionDTO);

        // TODO создания QR-code вынести во фронтенд?
        if (qr) {
            var qrData = webAppUrl.isBlank() ? botUrl + "?start=" + uuid : webAppUrl + "?startapp=" + uuid;
            var qrCode = qrCodeService.create(qrData, 200, 200);
            return Base64.getEncoder().encodeToString(qrCode);
        }

        return uuid;
    }
}
