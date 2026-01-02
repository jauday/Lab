package com.lab.patientservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

    // Configuración: 100 requests por minuto por IP
    private static final int MAX_REQUESTS = 100;
    private static final long TIME_WINDOW_MS = 60000; // 1 minuto

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String clientIP = getClientIP(request);

        if (!isAllowed(clientIP)) {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowed(String key) {
        long now = System.currentTimeMillis();

        requestCounts.computeIfAbsent(key, k -> new ArrayList<>());
        List<Long> timestamps = requestCounts.get(key);

        // Sincronizar para evitar race conditions
        synchronized (timestamps) {
            // Limpiar requests viejos
            timestamps.removeIf(t -> now - t > TIME_WINDOW_MS);

            if (timestamps.size() >= MAX_REQUESTS) {
                return false;
            }

            timestamps.add(now);
        }

        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}