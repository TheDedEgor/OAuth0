package com.example.oauth0.service;

import com.example.oauth0.apiClient.ApiClient;
import com.example.oauth0.model.dto.ErrorNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@RequiredArgsConstructor
public class ErrorNotificationService {
    private final ApiClient apiClient;

    public void sendNotification(String url, ErrorNotificationDto dto) {
        log.debug("Отправка уведомления об ошибке: {}", dto.getType());
        try {
            var response = apiClient.error(url, dto);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Уведомление успешно отправлено");
            } else {
                log.warn("Сервис уведомлений вернул ошибку: {}", response.getStatusCode());
                throw new RuntimeException("Не удалось отправить " + response.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("Ошибка HTTP при отправке уведомления", ex);
            throw new RuntimeException("Не удалось отправить уведомление", ex);
        }
    }

    @Async
    public CompletableFuture<Void> sendNotificationAsync(String url, ErrorNotificationDto dto) {
        return CompletableFuture.runAsync(() -> {
            sendNotification(url, dto);
        });
    }
}
