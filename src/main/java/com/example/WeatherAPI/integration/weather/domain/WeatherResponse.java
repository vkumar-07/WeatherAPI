package com.example.WeatherAPI.integration.weather.domain;


import lombok.Data;

@Data
public class WeatherResponse {
    private Location location;
    private Current current;
    private Forecast forecast;
}
