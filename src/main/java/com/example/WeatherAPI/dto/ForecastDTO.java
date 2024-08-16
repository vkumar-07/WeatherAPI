package com.example.WeatherAPI.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ForecastDTO {
    private List<ForecastDayDTO> forecastday;
}
