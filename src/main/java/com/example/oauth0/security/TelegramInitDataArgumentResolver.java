package com.example.oauth0.security;

import com.example.oauth0.model.dto.TelegramDataDTO;
import com.example.oauth0.service.TelegramInitDataValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TelegramInitDataArgumentResolver implements HandlerMethodArgumentResolver {

    @Value("${bot.token}")
    private String botToken;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(TelegramInitData.class)
                && parameter.getParameterType().equals(TelegramDataDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws JsonProcessingException {

        var headerName = parameter.getParameterAnnotation(TelegramInitData.class).headerName();

        var initData = webRequest.getHeader(headerName);
        if (initData == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пропущен initData заголовок");
        }
        if (!TelegramInitDataValidator.validate(initData, botToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недействительная initData сигнатура");
        }
        return TelegramInitDataValidator.parseToDto(initData);
    }
}
