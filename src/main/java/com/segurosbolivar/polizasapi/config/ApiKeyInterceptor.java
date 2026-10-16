package com.segurosbolivar.polizasapi.config;

import tools.jackson.databind.ObjectMapper;
import com.segurosbolivar.polizasapi.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String HEADER = "x-api-key";

    private final ObjectMapper objectMapper;

    @Value("${app.api-key}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String provided = request.getHeader(HEADER);
        if (apiKey.equals(provided)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiError error = new ApiError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                "Falta o es invalida la cabecera " + HEADER,
                Instant.now());
        objectMapper.writeValue(response.getWriter(), error);
        return false;
    }
}
