package com.example.telegramauth.bot;

import com.pengrad.telegrambot.model.Update;

public interface BotCommand {
    String command();
    void handle(Update update);
}
