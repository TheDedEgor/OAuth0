package com.example.telegramauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Базовые стандартные поля (если Spring их не добавил — добавляем сами)
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", "Validation failed for object='" +
                ex.getBindingResult().getObjectName() +
                "'. Error count: " + ex.getBindingResult().getErrorCount());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Собираем ошибки вида field -> message
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage(),
                        (msg1, msg2) -> msg1
                ));

        // Добавляем их в тело
        body.put("errors", fieldErrors);

        return new ResponseEntity<>(body, status);
    }
}
