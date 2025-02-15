package com.example.telegramauth.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import org.springframework.stereotype.Component;

@Component
public class BotExceptionHandler implements ExceptionHandler {
    @Override
    public void onException(TelegramException e) {
        if (e.response() != null) {
            // got bad response from telegram
            e.response().errorCode();
            e.response().description();
        } else {
            // probably network error
        }
    }
}
