package com.openweathermap.weather.service;

import com.openweathermap.weather.exception.OpenWeatherMapCallException;
import com.openweathermap.weather.model.Weather;
import com.openweathermap.weather.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WeatherServiceImplTest {

    @InjectMocks
    WeatherServiceImpl weatherService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    WeatherRepository weatherRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherService.url = "http://api.openweathermap.org/data/2.5/weather";
        weatherService.appId = "mockedApiKey";
    }

    @Test
    public void testGetCurrentWeatherByCity_WhenDataInDatabase_AndNotExpired() {
        Weather mockWeather = new Weather();
        mockWeather.setCityName("Sydney,AU");
        mockWeather.setWeather("{\"weather\": [{\"description\": \"clear sky\"}]}");
        mockWeather.setTime(System.currentTimeMillis());
        when(weatherRepository.findByCityName(eq("Sydney,AU"))).thenReturn(Optional.of(mockWeather));

        String result = weatherService.getCurrentWeatherByCity("Sydney,AU");

        assertEquals("clear sky", result);
        verify(weatherRepository, never()).delete(any());
        verify(restTemplate, never()).getForEntity(any(), eq(String.class));
    }

    @Test
    public void testGetCurrentWeatherByCity_WhenDataInDatabase_ButExpired() {
        Weather mockWeather = new Weather();
        mockWeather.setCityName("Sydney,AU");
        mockWeather.setWeather("{\"weather\": [{\"description\": \"clear sky\"}]}");
        mockWeather.setTime(System.currentTimeMillis() - 7 * 60 * 60 * 1000);

        when(weatherRepository.findByCityName(eq("Sydney,AU"))).thenReturn(Optional.of(mockWeather));

        String apiResponse = "{\"weather\": [{\"description\": \"rain\"}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(String.class))).thenReturn(responseEntity);

        String result = weatherService.getCurrentWeatherByCity("Sydney,AU");
        assertEquals("rain", result);

        verify(weatherRepository).delete(eq(mockWeather));
        verify(weatherRepository).save(any(Weather.class));
    }

    @Test
    public void testGetCurrentWeatherByCity_WhenDataNotInDatabase() {
        when(weatherRepository.findByCityName(eq("Sydney,AU"))).thenReturn(Optional.empty());

        String apiResponse = "{\"weather\": [{\"description\": \"cloudy\"}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(String.class))).thenReturn(responseEntity);

        String result = weatherService.getCurrentWeatherByCity("Sydney,AU");
        assertEquals("cloudy", result);

        verify(weatherRepository).save(any(Weather.class));
    }

    @Test
    public void testGetCurrentWeatherByCity_WhenApiCallFails() {
        when(weatherRepository.findByCityName(eq("Sydney,AU"))).thenReturn(Optional.empty());
        when(restTemplate.getForEntity(any(), eq(String.class))).thenThrow(new RuntimeException("API error"));

        OpenWeatherMapCallException exception = assertThrows(OpenWeatherMapCallException.class, () -> {
            weatherService.getCurrentWeatherByCity("Sydney,AU");
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode());
        assertEquals("API error", exception.getMessage());

        verify(weatherRepository, never()).save(any(Weather.class));
    }

    @Test
    public void testGetCurrentWeatherByCity_WithInvalidApiResponse() {
        when(weatherRepository.findByCityName(eq("Sydney,AU"))).thenReturn(Optional.empty());
        String invalidApiResponse = "{\"invalid\": \"response\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(any(), eq(String.class))).thenReturn(responseEntity);

        assertThrows(OpenWeatherMapCallException.class, () -> {
            weatherService.getCurrentWeatherByCity("Sydney,AU");
        });
    }
}
