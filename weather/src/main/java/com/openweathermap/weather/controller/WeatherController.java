package com.openweathermap.weather.controller;


import com.openweathermap.weather.service.WeatherService;
import com.openweathermap.weather.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {

    @Autowired
    WeatherService weatherService;

    /**
     * Getting current weather for requested city
     *
     * @param cityName
     * @return
     */
    @GetMapping("/currentweather")
    public ResponseEntity<String> getCurrentWeatherByCity(@RequestParam(value = "cityName") String cityName) {
        String weatherDescription = weatherService.getCurrentWeatherByCity(cityName);
        String responseBody = Utils.formatResponseBody(cityName, weatherDescription);
        return new ResponseEntity<String>(responseBody, HttpStatus.OK);
    }
}
