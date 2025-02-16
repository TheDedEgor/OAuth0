package com.example.telegramauth.bot;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserStateManager {
    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, String> userUuids  = new HashMap<>();

    public void setUserState(long chatId, UserState state) {
        userStates.put(chatId, state);
    }

    public UserState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, UserState.DEFAULT);
    }

    public void setUserUuid(long chatId, String uuid) {
        userUuids.put(chatId, uuid);
    }

    public String getUserUuid(long chatId) {
        return userUuids.get(chatId);
    }

    public void clearUserUuid(long chatId) {
        userUuids.remove(chatId);
    }

    public enum UserState {
        DEFAULT,
        SERVICE_AUTH
    }
}
