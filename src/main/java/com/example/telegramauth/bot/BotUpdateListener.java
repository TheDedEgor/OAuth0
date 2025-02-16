package com.example.telegramauth.bot;

import com.example.telegramauth.api.ApiService;
import com.example.telegramauth.model.dto.UserDTO;
import com.example.telegramauth.service.ParamsService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Log4j2
@Component
public class BotUpdateListener implements UpdatesListener {

    @Value("${bot.enableConfirm}")
    private Boolean enableConfirm;

    private final TelegramBot bot;

    private final UserStateManager userStateManager;

    private final ApiService apiService;

    private final ParamsService paramsService;

    private static final String BOT_ERROR_MESSAGE = "Во время авторизации произошла ошибка! Обратитесь к администратору!";

    public BotUpdateListener(TelegramBot bot, UserStateManager userStateManager, ApiService apiService, ParamsService paramsService) {
        this.bot = bot;
        this.apiService = apiService;
        this.userStateManager = userStateManager;
        this.paramsService = paramsService;
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::handleUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(Update update) {
        var chatId = update.message().chat().id();
        var messageText = update.message().text().trim();
        var state = userStateManager.getUserState(chatId);

        switch (state) {
            case DEFAULT -> defaultHandler(chatId, messageText);
            case SERVICE_AUTH -> serviceAuthHandler(chatId, messageText);
        }
    }

    private void defaultHandler(long chatId, String text) {
        if (text.startsWith("/start")) {
            try {
                var optionalParamsId = getParamsId(text);
                if (optionalParamsId.isEmpty()) {
                    log.error("ID не был передан боту");
                    bot.execute(new SendMessage(chatId, BOT_ERROR_MESSAGE));
                    return;
                }
                var id = optionalParamsId.get();
                userStateManager.setParamsId(chatId, id);
                var params = paramsService.get(id);

                if (enableConfirm) {
                    var keyboard = new ReplyKeyboardMarkup(
                            new KeyboardButton("Да"),
                            new KeyboardButton("Нет")
                    ).resizeKeyboard(true);

                    var message = new SendMessage(chatId, getConfirmAuthMessage(params.getServiceName()))
                            .replyMarkup(keyboard);
                    bot.execute(message);
                    userStateManager.setUserState(chatId, UserStateManager.UserState.SERVICE_AUTH);
                } else {
                    auth(chatId);
                    userStateManager.clearParamsId(chatId);
                }
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
                bot.execute(new SendMessage(chatId, BOT_ERROR_MESSAGE));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Неизвестная команда!"));
        }
    }

    private void serviceAuthHandler(long chatId, String text) {
        text = text.toLowerCase();
        if (text.equals("да")) {
            auth(chatId);
            userStateManager.setUserState(chatId, UserStateManager.UserState.DEFAULT);
            userStateManager.clearParamsId(chatId);
        } else if (text.equals("нет")) {
            var message = new SendMessage(chatId, "Вход отменен")
                    .replyMarkup(new ReplyKeyboardRemove(true));
            bot.execute(message);
            userStateManager.setUserState(chatId, UserStateManager.UserState.DEFAULT);
            userStateManager.clearParamsId(chatId);
        } else {
            var message = new SendMessage(chatId, "Подтвердите вход в сервис!");
            bot.execute(message);
        }
    }

    private Optional<Long> getParamsId(String text) {
        var parts = text.split("\\s+", 2);
        if (parts.length > 1) {
            return Optional.of(Long.parseLong(parts[1]));
        }
        return Optional.empty();
    }

    private void auth(long chatId) {
        try {
            var id = userStateManager.getParamsIds(chatId);
            var params = paramsService.get(id);

            bot.execute(new SendMessage(chatId, "Авторизуемся..."));

            var response = apiService.auth(params.getAuthUrl(), new UserDTO(chatId));
            if (response.getStatusCode().is2xxSuccessful()) {
                var message = new SendMessage(chatId, "Авторизация прошла успешно!")
                        .replyMarkup(new ReplyKeyboardRemove(true));
                bot.execute(message);
            } else {
                var message = new SendMessage(chatId, "Не удалось пройти авторизацию!")
                        .replyMarkup(new ReplyKeyboardRemove(true));
                bot.execute(message);
            }
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            var message = new SendMessage(chatId, "Не удалось пройти авторизацию!")
                    .replyMarkup(new ReplyKeyboardRemove(true));
            bot.execute(message);
        }
    }

    private String getConfirmAuthMessage(String serviceName) {
        if (serviceName == null || serviceName.isEmpty()) {
            return "Вы подтверждаете вход?";
        }
        return "Вы подтверждаете вход в сервис: " + serviceName + "?";
    }
}
