package com.openweathermap.weather.exception;

import org.springframework.http.HttpStatusCode;

public class OpenWeatherMapCallException extends RuntimeException {
    private String errorMessage;
    private HttpStatusCode errorCode;

    public OpenWeatherMapCallException(String errorMessage, HttpStatusCode errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }
}
