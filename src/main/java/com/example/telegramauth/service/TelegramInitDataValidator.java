package com.example.telegramauth.service;

import com.example.telegramauth.model.dto.TelegramDataDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.User;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class TelegramInitDataValidator {

    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY) // доступ к приватным полям
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE) // игнорируем геттеры
            .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE); // игнорируем сеттеры

    public static boolean validate(String initData, String botToken) {
        var params = parseQuery(initData);

        var receivedHash = params.remove("hash");
        if (receivedHash == null) return false;

        var dataCheckString = buildDataCheckString(params);

        try {
            var secretKey = hmacSha256(botToken.getBytes(StandardCharsets.UTF_8), "WebAppData".getBytes(StandardCharsets.UTF_8));
            var finalHmac = hmacSha256(dataCheckString.getBytes(StandardCharsets.UTF_8), secretKey);

            var calculatedHash = toHex(finalHmac);
            return calculatedHash.equalsIgnoreCase(receivedHash);
        } catch (Exception ex) {
            throw new RuntimeException("HMAC error", ex);
        }
    }

    public static TelegramDataDTO parseToDto(String initData) throws JsonProcessingException {
        var map = parseQuery(initData);
        var telegramDataDTO = new TelegramDataDTO();

        telegramDataDTO.setAuthDate(Long.parseLong(map.get("auth_date")));
        telegramDataDTO.setStartParam(map.get("start_param"));
        // TODO сделать отдельный класс User для маппинга данных с web app?
        telegramDataDTO.setUser(objectMapper.readValue(map.get("user"), User.class));

        return telegramDataDTO;
    }

    private static Map<String, String> parseQuery(String qs) {
        var decoded = URLDecoder.decode(qs, StandardCharsets.UTF_8);
        return UriComponentsBuilder.newInstance()
                .query(decoded)
                .build()
                .getQueryParams()
                .toSingleValueMap();
    }

    private static String buildDataCheckString(Map<String, String> params) {
        return params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(item -> item.getKey() + "=" + item.getValue())
                .collect(Collectors.joining("\n"));
    }

    private static byte[] hmacSha256(byte[] data, byte[] key) throws Exception {
        var mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
