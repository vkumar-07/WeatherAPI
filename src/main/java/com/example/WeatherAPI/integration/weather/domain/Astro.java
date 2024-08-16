package com.example.WeatherAPI.integration.weather.domain;

import lombok.Data;

@Data
public class Astro {
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private String moon_phase;
    private String moon_illumination;
}
