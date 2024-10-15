package com.openweathermap.weather.util;

public class Constant {
    public static final String REQUEST_QUERY_PARAM = "q";
    public static final String REQUEST_APPID_PARAM = "appid";
    public static final String WEATHER_PARAM = "weather";
    public static final String DESCRIPTION_PARAM = "description";
    public static final String X_API_KEY = "x-api-key";
    public static final int REQUESTS_RATE_PER_HOUR = 5;
    public static final int TTL_FOR_DATA_STORED = 6;
    public static final String ERR_MSG_MISSING_API_KEY = "Missing API Key, please provide a valid x-api-key in the request header.";
    public static final String ERR_MSG_EXCEEDED_RATE_LIMIT = "Too Many Requests - Rate limit exceeded, limit rate 5 requests per hour for each API key.";
    public static final String ERR_MSG_INVALID_API_KEY = "Invalid API Key";
    public static final String ERR_MSG_OPENWEATHERMAP_CALL_ISSUE = "Error occurred while calling OpenWeatherMap API. " +
            "\nError message from OpenWeatherMap:\n";
    public static final String ERR_MSG_OPENWEATHERMAP_INVALID_RESPONSE = "Could not found current weather for requested city.";
}
