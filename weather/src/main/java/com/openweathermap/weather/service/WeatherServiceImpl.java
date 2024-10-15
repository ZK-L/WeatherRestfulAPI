package com.openweathermap.weather.service;


import com.openweathermap.weather.exception.OpenWeatherMapCallException;
import com.openweathermap.weather.model.Weather;
import com.openweathermap.weather.repository.WeatherRepository;
import com.openweathermap.weather.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static com.openweathermap.weather.util.Constant.*;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${openWeatherMap.weather}")
    String url;
    @Value("${openWeatherMap.apiKey}")
    String appId;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    WeatherRepository weatherRepository;

    /**
     * Check weather from H2 DB first, if the weather data for requesting city exist
     * and data was created within 6 hours, use the data from DB. Otherwise, sending
     * request to OpenWeatherMap API to get new data and save to DB.
     *
     * @param city
     * @return
     */
    @Override
    public String getCurrentWeatherByCity(String city) {
        Optional<Weather> storedWeather = weatherRepository.findByCityName(city);
        Weather weather = null;

        if (storedWeather.isPresent()) {
            int timeDiff = Utils.hourDiffWithCurrent(storedWeather.get().getTime());
            if (timeDiff < TTL_FOR_DATA_STORED) {
                weather = storedWeather.get();
            } else {
                weatherRepository.delete(storedWeather.get());
            }
        }

        if (weather == null) {
            weather = new Weather();
            URI uri = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam(REQUEST_QUERY_PARAM, city)
                    .queryParam(REQUEST_APPID_PARAM, appId)
                    .build().toUri();
            ResponseEntity<String> weatherResponse;

            try {
                weatherResponse = restTemplate.getForEntity(uri, String.class);
            } catch (Exception e) {
                throw new OpenWeatherMapCallException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            weather.setWeather(weatherResponse.getBody());
            weather.setCityName(city);
            weather.setTime(System.currentTimeMillis());
            weatherRepository.save(weather);
        }

        String weatherDescription;
        try {
            weatherDescription = new JSONObject(weather.getWeather())
                    .optJSONArray(WEATHER_PARAM)
                    .optJSONObject(0)
                    .optString(DESCRIPTION_PARAM);
        } catch (Exception e) {
            weatherRepository.delete(weather);
            throw new OpenWeatherMapCallException(ERR_MSG_OPENWEATHERMAP_INVALID_RESPONSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return weatherDescription;
    }
}
