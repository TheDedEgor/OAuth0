package com.example.telegramauth.bot;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserStateManager {
    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, Long> userParamIds = new HashMap<>();

    public void setUserState(long chatId, UserState state) {
        userStates.put(chatId, state);
    }

    public UserState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, UserState.DEFAULT);
    }

    public void setParamsId(long chatId, long id) {
        userParamIds.put(chatId, id);
    }

    public Long getParamsIds(long chatId) {
        return userParamIds.get(chatId);
    }

    public void clearParamsId(long chatId) {
        userParamIds.remove(chatId);
    }

    public enum UserState {
        DEFAULT,
        SERVICE_AUTH
    }
}
