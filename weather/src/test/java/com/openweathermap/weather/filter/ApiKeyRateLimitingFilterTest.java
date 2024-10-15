package com.openweathermap.weather.filter;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

import static com.openweathermap.weather.util.Constant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ApiKeyRateLimitingFilterTest {

    @InjectMocks
    private ApiKeyRateLimitingFilter apiKeyRateLimitingFilter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain mockFilterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        apiKeyRateLimitingFilter.validApiKeys = Arrays.asList("apikey1", "apikey2", "apikey3", "apikey4", "apikey5");
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockFilterChain = new MockFilterChain();
    }

    @Test
    public void testMissingApiKey() throws ServletException, IOException {
        apiKeyRateLimitingFilter.doFilterInternal(request, response, mockFilterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(ERR_MSG_MISSING_API_KEY, response.getContentAsString());
    }

    @Test
    public void testInvalidApiKey() throws ServletException, IOException {
        request.addHeader(X_API_KEY, "INVALID_API_KEY");
        apiKeyRateLimitingFilter.doFilterInternal(request, response, mockFilterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(ERR_MSG_INVALID_API_KEY, response.getContentAsString());
    }

    @Test
    public void testValidApiKeyWithinLimit() throws ServletException, IOException {
        request.addHeader(X_API_KEY, "apikey1");
        apiKeyRateLimitingFilter.doFilterInternal(request, response, filterChain);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testValidApiKeyExceededLimit() throws ServletException, IOException {
        String validApiKey = "apikey1";
        RateLimiter rateLimiter = RateLimiter.create((double) REQUESTS_RATE_PER_HOUR / 3600);
        apiKeyRateLimitingFilter.rateLimiters.put(validApiKey, rateLimiter);
        for (int i = 0; i < REQUESTS_RATE_PER_HOUR; i++) {
            rateLimiter.tryAcquire();
        }

        request.addHeader(X_API_KEY, validApiKey);
        apiKeyRateLimitingFilter.doFilterInternal(request, response, filterChain);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
        assertEquals(ERR_MSG_EXCEEDED_RATE_LIMIT, response.getContentAsString());
    }
}
