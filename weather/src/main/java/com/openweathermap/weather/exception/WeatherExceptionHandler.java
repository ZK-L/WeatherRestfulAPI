package com.openweathermap.weather.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.openweathermap.weather.util.Constant.ERR_MSG_OPENWEATHERMAP_CALL_ISSUE;

@RestControllerAdvice
public class WeatherExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> MissingRequestParameterException(MissingServletRequestParameterException ex){
        return new ResponseEntity<>("ERROR: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OpenWeatherMapCallException.class)
    public ResponseEntity<String> ErrorCallingOpenWeatherMapAPI(OpenWeatherMapCallException ex){
        return new ResponseEntity<>(ERR_MSG_OPENWEATHERMAP_CALL_ISSUE + ex.getMessage(), ex.getErrorCode());
    }
}
