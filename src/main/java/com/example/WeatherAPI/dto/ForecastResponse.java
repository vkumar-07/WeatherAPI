package com.example.WeatherAPI.dto;

import com.example.WeatherAPI.integration.weather.domain.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForecastResponse {

    private Location location;
    private CurrentDTO current;
    private ForecastDTO forecast;

}
