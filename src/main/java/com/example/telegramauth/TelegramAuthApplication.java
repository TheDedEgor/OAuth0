package com.example.telegramauth;

import com.example.telegramauth.bot.BotConfig;
import com.example.telegramauth.bot.BotExceptionHandler;
import com.example.telegramauth.bot.BotUpdateListener;
import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

@Log4j2
@SpringBootApplication
@EnableConfigurationProperties(BotConfig.class)
public class TelegramAuthApplication {

    private final TelegramBot bot;
    private final BotUpdateListener botUpdateListener;
    private final BotExceptionHandler botExceptionHandler;

    public TelegramAuthApplication(TelegramBot bot, BotUpdateListener botUpdateListener, BotExceptionHandler
                                   botExceptionHandler) {
        this.bot = bot;
        this.botUpdateListener = botUpdateListener;
        this.botExceptionHandler = botExceptionHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(TelegramAuthApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runBot() {
        bot.setUpdatesListener(botUpdateListener, botExceptionHandler);
        log.info("Бот запущен");
    }
}
