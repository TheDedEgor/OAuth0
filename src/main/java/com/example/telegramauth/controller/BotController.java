package com.example.telegramauth.controller;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import com.example.telegramauth.service.ParamsService;
import com.example.telegramauth.service.QrCodeService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/bot")
public class BotController {

    @Value("${bot.url}")
    private String botUrl;

    private final ParamsService paramsService;

    private final QrCodeService qrCodeService;

    public BotController(ParamsService paramsService, QrCodeService qrCodeService) {
        this.paramsService = paramsService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/addInfo")
    public String addInfo(@Valid @RequestBody ServiceParamsDTO params) {
        return paramsService.create(params);
    }

    @PostMapping("/generateQrCode")
    public String generateQrCode(@Valid @RequestBody ServiceParamsDTO params) throws IOException, WriterException {
        var id = paramsService.create(params);
        var text = botUrl + "?start=" + id;
        var qrCode = qrCodeService.generateQrCode(text, 200, 200);
        return Base64.getEncoder().encodeToString(qrCode);
    }
}
