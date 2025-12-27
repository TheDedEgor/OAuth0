package com.example.oauth0.service;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureTokenGenerator {
    private static final int STATE_TOKEN_LENGTH = 32;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String create() {
        byte[] tokenBytes = new byte[STATE_TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
