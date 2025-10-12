package com.example.telegramauth.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class BotUpdateListener implements UpdatesListener {

    private final TelegramBot bot;

    private final Map<String, BotCommand> commandMap;

    public BotUpdateListener(TelegramBot bot, List<BotCommand> commands) {
        this.bot = bot;
        this.commandMap = commands.stream()
                .collect(Collectors.toMap(BotCommand::command, Function.identity()));
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::handleUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(Update update) {
        var chatId = update.message().chat().id();
        var text = update.message().text().trim().split(" ")[0];

        var command = commandMap.get(text);

        if (command != null) {
            command.handle(update);
        } else {
            bot.execute(new SendMessage(chatId, "Неизвестная команда!"));
        }
    }
}
