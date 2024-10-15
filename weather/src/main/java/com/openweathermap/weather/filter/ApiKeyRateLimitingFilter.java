package com.openweathermap.weather.filter;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.openweathermap.weather.util.Constant.*;

@Component
public class ApiKeyRateLimitingFilter extends OncePerRequestFilter {

    @Value("${api.keys}")
    List<String> validApiKeys;
    final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    /**
     * ApiKeyRateLimitingFilter to limit the request rate for each API key as 5 per hour
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(X_API_KEY);

        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ERR_MSG_MISSING_API_KEY);
            return;
        }

        if (validApiKeys.contains(apiKey)) {
            RateLimiter rateLimiter = rateLimiters.computeIfAbsent(apiKey, key -> RateLimiter.create((double) REQUESTS_RATE_PER_HOUR / 3600));
            if (rateLimiter.tryAcquire()) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write(ERR_MSG_EXCEEDED_RATE_LIMIT);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ERR_MSG_INVALID_API_KEY);
        }
    }


}
