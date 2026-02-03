package com.example.oauth0.aspect;

import com.example.oauth0.exception.SessionException;
import com.example.oauth0.model.dto.ErrorNotificationDto;
import com.example.oauth0.service.ErrorNotificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ExceptionNotificationAspect {

    private final ErrorNotificationService errorNotificationService;

    @AfterThrowing(
        pointcut = "within(@org.springframework.stereotype.Service *)",
        throwing = "ex"
    )
    public void handleSessionExceptionsInServices(JoinPoint joinPoint, SessionException ex) {
        var errorNotificationDto = new ErrorNotificationDto(ex);
        var url = ex.getErrorUrl();
        errorNotificationService.sendNotificationAsync(url, errorNotificationDto);
    }
}
