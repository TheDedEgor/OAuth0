package com.example.telegramauth;

import com.example.telegramauth.service.TelegramInitDataValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TelegramInitDataValidatorTest {
    private static final String TEST_INIT_DATA = "query_id=AAHdF6IQAAAAAN0XohDhrOrc&user=%7B%22id%22%3A279058397%2C%22first_name%22%3A%22Vladislav%22%2C%22last_name%22%3A%22Kibenko%22%2C%22username%22%3A%22vdkfrost%22%2C%22language_code%22%3A%22ru%22%2C%22is_premium%22%3Atrue%7D&auth_date=1662771648&hash=c501b71e775f74ce10e377dea85a7ea24ecd640b223ea86dfe453e0eaed2e2b2";
    private static final String TEST_BOT_TOKEN = "5768337691:AAH5YkoiEuPk8-FZa32hStHTqXiLPtAEhx8";

    @Test
    public void checkValidate() {
        var result = TelegramInitDataValidator.validate(TEST_INIT_DATA, TEST_BOT_TOKEN);
        assertTrue(result);
    }
}
