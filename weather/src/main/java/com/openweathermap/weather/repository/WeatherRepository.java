package com.openweathermap.weather.repository;

import com.openweathermap.weather.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findByCityName(String cityName);
}
