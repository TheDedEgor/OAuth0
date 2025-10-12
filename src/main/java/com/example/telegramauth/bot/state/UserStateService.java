package com.example.telegramauth.bot.state;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStateService {
    private final Map<Long, BotState> userStates = new ConcurrentHashMap<>();

    public void setState(long chatId, BotState state) {
        userStates.put(chatId, state);
    }

    public BotState getState(long chatId) {
        return userStates.getOrDefault(chatId, BotState.DEFAULT);
    }
}
