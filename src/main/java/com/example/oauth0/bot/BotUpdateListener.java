package com.example.oauth0.bot;

import com.example.oauth0.model.enums.SessionStatus;
import com.example.oauth0.service.AuthSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
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

    private final AuthSessionService authSessionService;

    private final Map<String, BotCommand> commandMap;

    public BotUpdateListener(TelegramBot bot, AuthSessionService authSessionService, List<BotCommand> commands) {
        this.bot = bot;
        this.authSessionService = authSessionService;
        this.commandMap = commands.stream()
                .collect(Collectors.toMap(BotCommand::command, Function.identity()));
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::handleUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(Update update) {
        if (update.callbackQuery() != null) {
            handleCallbackQuery(update.callbackQuery());
            return;
        }

        var chatId = update.message().chat().id();
        var text = update.message().text().trim().split(" ")[0];

        var command = commandMap.get(text);

        if (command != null) {
            command.handle(update);
        } else {
            bot.execute(new SendMessage(chatId, "Неизвестная команда!"));
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var message = callbackQuery.message();
        if (message == null) return;

        var callbackId = callbackQuery.id();
        var callbackData = callbackQuery.data();
        var providerId = callbackQuery.from().id();
        var chatId = message.chat().id();

        var parts = callbackData.split(":");
        var action = parts[0];
        var uuid = parts[1];

        // Отвечаем на callback (убираем "часики" на кнопке)
        var answer = new AnswerCallbackQuery(callbackId);
        bot.execute(answer);

        // Редактируем сообщение - удаляем разметку клавиатуры
        var editMessage = new EditMessageText(
            chatId,
            message.messageId(),
            message.text()
        );
        // Не устанавливаем replyMarkup - кнопки исчезнут
        bot.execute(editMessage);

        var isConfirmed = false;
        switch (action) {
            case "confirm_session":
                isConfirmed = true;
                break;
            case "revoke_session":
                break;
        }

        try {
            var sessionStatus = authSessionService.confirm(uuid, providerId, isConfirmed);
            if (sessionStatus == SessionStatus.CONFIRMED) {
                bot.execute(new SendMessage(chatId, "Входим..."));
                authSessionService.auth(uuid, providerId);
                bot.execute(new SendMessage(chatId, "Вход успешно выполнен!"));
            } else {
                bot.execute(new SendMessage(chatId, "Вход отменен!"));
            }
        } catch (Exception ex) {
            bot.execute(new SendMessage(chatId, "Во время входа произошла ошибка!"));
        }
    }
}
