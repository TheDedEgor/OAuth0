package com.example.telegramauth.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");
        String path = request.getRequestURI();

        log.info("Запрос к {}, IP: {}, User-Agent: {}", path, ip, userAgent);

        chain.doFilter(servletRequest, servletResponse);
    }
}
