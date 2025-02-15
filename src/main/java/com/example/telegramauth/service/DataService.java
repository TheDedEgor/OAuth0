package com.example.telegramauth.service;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataService {
    private final Map<String, ServiceParamsDTO> data = new ConcurrentHashMap<>();

    public void add(String uuid, ServiceParamsDTO params) {
       data.put(uuid, params);
    }

    public ServiceParamsDTO get(String uuid) {
        return data.get(uuid);
    }

    public void remove(String uuid) {
        data.remove(uuid);
    }
}
