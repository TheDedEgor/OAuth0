package com.example.oauth0;

import com.example.oauth0.bot.BotExceptionHandler;
import com.example.oauth0.bot.BotUpdateListener;
import com.example.oauth0.config.BotConfig;
import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(BotConfig.class)
@RequiredArgsConstructor
public class TelegramAuthApplication {

    private final TelegramBot bot;
    private final BotUpdateListener botUpdateListener;
    private final BotExceptionHandler botExceptionHandler;

    public static void main(String[] args) {
        SpringApplication.run(TelegramAuthApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runBot() {
        bot.setUpdatesListener(botUpdateListener, botExceptionHandler);
        log.info("Бот запущен");
    }
}
