package com.example.oauth0.service;

import jakarta.servlet.http.Cookie;

import java.time.Duration;
import java.time.ZonedDateTime;

public class CookieService {
    public static Cookie createForSecureToken(String secureToken, ZonedDateTime expiredAt) {
        var cookie = new Cookie("OAUTH_SECURE_TOKEN", secureToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        var seconds = Duration.between(ZonedDateTime.now(), expiredAt).getSeconds();
        cookie.setMaxAge((int) seconds);
        return cookie;
    }
}
