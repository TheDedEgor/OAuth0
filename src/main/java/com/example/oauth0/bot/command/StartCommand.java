package com.example.oauth0.bot.command;

import com.example.oauth0.bot.BotCommand;
import com.example.oauth0.model.enums.SessionStatus;
import com.example.oauth0.service.AuthSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class StartCommand implements BotCommand {

    private final TelegramBot bot;

    private final AuthSessionService authSessionService;

    private static final String BOT_ERROR_MESSAGE = "Во время авторизации произошла ошибка! Обратитесь к администратору!";

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public void handle(Update update) {
        var chatId = update.message().chat().id();
        var text = update.message().text().trim();

        try {
            var optionalUuid = getUuid(text);
            if (optionalUuid.isEmpty()) {
                log.error("UUID не был передан боту");
                bot.execute(new SendMessage(chatId, BOT_ERROR_MESSAGE));
                return;
            }
            var uuid = optionalUuid.get();

            auth(uuid, update);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            bot.execute(new SendMessage(chatId, BOT_ERROR_MESSAGE));
        }
    }

    private void auth(String uuid, Update update) {
        var chatId = update.message().chat().id();
        try {
            var user = update.message().from();
            var providerId = user.id();

            var serviceInfo = authSessionService.link(uuid, providerId);
            var sessionStatus = serviceInfo.getSessionStatus();

            var infoServiceMsg = String.format("<u>Информация о сервисе:</u>\n<b>%s</b>", serviceInfo.getName())
                + (serviceInfo.getDescription() != null ? String.format("\n<i>%s</i>", serviceInfo.getDescription()) : "");
            bot.execute(new SendMessage(chatId, infoServiceMsg).parseMode(ParseMode.HTML));
            if (sessionStatus == SessionStatus.AWAITING_CONFIRMATION) {
                // Создаем кнопки
                var btn1 = new InlineKeyboardButton("Да")
                    .callbackData("confirm_session:" + uuid);
                var btn2 = new InlineKeyboardButton("Нет")
                    .callbackData("revoke_session:" + uuid);

                // Создаем клавиатуру с двумя кнопками в одном ряду
                var keyboardMarkup = new InlineKeyboardMarkup(btn1, btn2);

                bot.execute(new SendMessage(chatId, "Вы уверены, что хотите войти в данный сервис?").replyMarkup(keyboardMarkup));

                return;
            }
            bot.execute(new SendMessage(chatId, "Входим..."));
            authSessionService.auth(uuid, providerId);
            bot.execute(new SendMessage(chatId, "Вход успешно выполнен!"));
        } catch (Exception ex) {
            log.error(ex);
            bot.execute(new SendMessage(chatId, "Во время входа произошла ошибка!"));
        }
    }

    /**
     * Получение uuid из текста клиента telegram
     *
     * @param text текст клиента telegram
     * @return Uuid если он есть
     */
    private Optional<String> getUuid(String text) {
        var parts = text.split("\\s+", 2);
        if (parts.length > 1) {
            return Optional.of(parts[1]);
        }
        return Optional.empty();
    }
}
