package com.openweathermap.weather.controller;

import com.openweathermap.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.openweathermap.weather.util.Constant.ERR_MSG_MISSING_API_KEY;
import static com.openweathermap.weather.util.Constant.X_API_KEY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void testGetCurrentWeatherByCity_Success() throws Exception {
        String cityName = "Sydney,AU";
        String weatherDescription = "broken clouds";
        String expectedResponse = "Current Weather in Sydney,AU: broken clouds";

        when(weatherService.getCurrentWeatherByCity(cityName)).thenReturn(weatherDescription);
        mockMvc.perform(get("/api/currentweather")
                        .param("cityName", cityName)
                        .header(X_API_KEY, "apikey1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
        verify(weatherService).getCurrentWeatherByCity(cityName);
    }

    @Test
    void testGetCurrentWeatherByCity_NoApiKey() throws Exception {
        String cityName = "Sydney,AU";
        String expectedResponse = ERR_MSG_MISSING_API_KEY;

        when(weatherService.getCurrentWeatherByCity(cityName)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/currentweather")
                        .param("cityName", cityName))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedResponse));
    }


    @Test
    public void testGetCurrentWeather_EmptyCityName() throws Exception {
        String expectedResponse = "ERROR: Required request parameter 'cityName' for method parameter type String is not present";
        mockMvc.perform(get("/api/currentweather")
                        .header("x-api-key", "apikey2"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedResponse));
    }

}