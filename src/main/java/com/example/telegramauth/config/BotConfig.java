package com.example.telegramauth.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "bot")
public record BotConfig(@NotEmpty String token) {

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(token);
    }
}
