package com.example.oauth0.bot.command;

import com.example.oauth0.apiClient.AuthApiClient;
import com.example.oauth0.bot.BotCommand;
import com.example.oauth0.model.dto.UserDTO;
import com.example.oauth0.service.AuthSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
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

    private final AuthApiClient authApiClient;

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
            if (optionalUuid .isEmpty()) {
                log.error("UUID не был передан боту");
                bot.execute(new SendMessage(chatId, BOT_ERROR_MESSAGE));
                return;
            }
            var uuid = optionalUuid.get();

            auth(uuid, update);
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            bot.execute(new SendMessage(chatId, BOT_ERROR_MESSAGE));
        }
    }

    private void auth(String uuid, Update update) {
        var chatId = update.message().chat().id();
        try {
            var session = authSessionService.get(uuid);
            var user = update.message().from();
            var serviceConfig = session.getExternalServiceConfig();

            var infoServiceMsg = String.format("<u>Информация о сервисе:</u>\n<b>%s</b>", serviceConfig.getServiceName())
                + (serviceConfig.getDescription() != null ? String.format("\n<i>%s</i>", serviceConfig.getDescription()) : "");
            bot.execute(new SendMessage(chatId, infoServiceMsg).parseMode(ParseMode.HTML));
            bot.execute(new SendMessage(chatId, "Авторизуемся..."));

            var response = authApiClient.auth(serviceConfig.getAuthUrl(), new UserDTO(uuid, user));
            if (response.getStatusCode().is2xxSuccessful()) {
                var contentType = response.getHeaders().getContentType();
                if (contentType != null) {
                    throw new RuntimeException(String.format("Сервер вернул неожиданный Content-Type: %s", contentType));
                }

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
            log.error(ex);
            var message = new SendMessage(chatId, "Не удалось пройти авторизацию!")
                    .replyMarkup(new ReplyKeyboardRemove(true));
            bot.execute(message);
        }
    }

    /**
     * Получение uuid из текста клиента telegram
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
