package com.example.WeatherAPI.integration.weather.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ForecastDay {
    private String date;
    private long date_epoch;
    private Day day;
    private Astro astro;
    private List<Hour> hour;
}
