package com.openweathermap.weather.util;

public class Utils {
    public static int hourDiffWithCurrent(long time){
        long currentTime = System.currentTimeMillis() / (1000 * 60 * 60);
        int timeDiff = (int) (currentTime-time/(1000 * 60 * 60));
        return timeDiff;
    }

    public static String formatResponseBody(String cityName, String weatherDescription){
        return "Current Weather in " + cityName + ": " + weatherDescription;
    }
}
