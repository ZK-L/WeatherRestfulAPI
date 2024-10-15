package com.openweathermap.weather.model;


import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.persistence.*;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

@Table(name="cityWeather")
@Entity
@Getter
@Setter
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String cityName;
    @Lob
    private String weather;
    private long time;
}
