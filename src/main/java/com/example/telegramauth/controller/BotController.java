package com.example.telegramauth.controller;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import com.example.telegramauth.service.DataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bot")
public class BotController {

    private final DataService dataService;

    public BotController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/addInfo")
    public String addInfo(@Valid @RequestBody ServiceParamsDTO params) {
        var uuid = UUID.randomUUID().toString();
        dataService.add(uuid, params);
        return uuid;
    }
}
