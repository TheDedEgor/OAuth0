package com.example.telegramauth.apiClient;

import com.example.telegramauth.model.dto.UserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthApiClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<Void> auth(String url, UserDTO userDTO) {
        var requestEntity = new HttpEntity<>(userDTO, null);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }
}
